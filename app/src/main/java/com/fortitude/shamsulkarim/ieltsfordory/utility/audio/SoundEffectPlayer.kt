package com.fortitude.shamsulkarim.ieltsfordory.utility.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.fortitude.shamsulkarim.ieltsfordory.R

/**
 * Low-latency audio effect player for quiz interactions and session completion.
 * Utilizes Android's SoundPool to play raw sound effects when sound is enabled.
 */
class SoundEffectPlayer(context: Context) {

    private val soundPool: SoundPool
    private val correctSoundId: Int
    private val incorrectSoundId: Int
    private val finishedSoundId: Int

    private val loadedSoundIds = mutableSetOf<Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSoundIds.add(sampleId)
            }
        }

        val appContext = context.applicationContext
        correctSoundId = soundPool.load(appContext, R.raw.correct, 1)
        incorrectSoundId = soundPool.load(appContext, R.raw.incorrect, 1)
        finishedSoundId = soundPool.load(appContext, R.raw.train_finished, 1)
    }

    fun playCorrect() {
        playSound(correctSoundId)
    }

    fun playIncorrect() {
        playSound(incorrectSoundId)
    }

    fun playSessionComplete() {
        playSound(finishedSoundId)
    }

    private fun playSound(soundId: Int) {
        if (loadedSoundIds.contains(soundId)) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
