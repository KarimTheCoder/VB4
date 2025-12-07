package com.fortitude.shamsulkarim.ieltsfordory.data.tts

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.TtsRepository
import java.util.Locale
import java.util.UUID

class  AndroidTtsRepository(private val context: Context) : TtsRepository, TextToSpeech.OnInitListener {
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var tts: TextToSpeech = TextToSpeech(context.applicationContext, this)
    @Volatile private var ready: Boolean = false
    private var focusRequest: AudioFocusRequest? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            ready = false
            return
        }
        val res = tts.setLanguage(Locale.US)
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            ready = false
            return
        }
        if (Build.VERSION.SDK_INT >= 21) {
            tts.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
        }
        tts.setSpeechRate(1.0f)
        tts.setPitch(1.0f)
        ready = true
    }

    override fun isReady(): Boolean = ready

    override fun speak(text: String, flush: Boolean) {
        if (!ready || text.isEmpty()) return
        requestFocus()
        val id = "utt-${UUID.randomUUID()}"
        if (Build.VERSION.SDK_INT >= 21) {
            val params = Bundle()
            params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
            tts.speak(text, if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, params, id)
        } else {
            @Suppress("DEPRECATION")
            tts.speak(text, if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null)
        }
    }

    override fun stop() {
        tts.stop()
        abandonFocus()
    }

    override fun shutdown() {
        tts.stop()
        tts.shutdown()
        ready = false
        abandonFocus()
    }

    private fun requestFocus() {
        if (Build.VERSION.SDK_INT >= 26) {
            if (focusRequest == null) {
                focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    ).build()
            }
            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        }
    }

    private fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= 26) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }
}

