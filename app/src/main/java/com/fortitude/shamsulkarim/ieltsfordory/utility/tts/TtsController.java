package com.fortitude.shamsulkarim.ieltsfordory.utility.tts;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import java.util.UUID;

public final class TtsController implements TextToSpeech.OnInitListener {
    private final Context context;
    private final AudioManager audioManager;
    private TextToSpeech tts;
    private boolean ready;
    private AudioFocusRequest focusRequest;

    public TtsController(Context ctx) {
        this.context = ctx.getApplicationContext();
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.tts = new TextToSpeech(context, this);
        this.ready = false;
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            ready = false;
            return;
        }
        int res = tts.setLanguage(Locale.US);
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            ready = false;
            return;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            tts.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build());
        }
        tts.setSpeechRate(1.0f);
        tts.setPitch(1.0f);
        ready = true;
    }

    public boolean isReady() {
        return ready;
    }

    public void speak(String text, boolean flush) {
        if (!ready || tts == null || text == null || text.isEmpty()) return;
        requestFocus();
        String id = "utt-" + UUID.randomUUID();
        if (Build.VERSION.SDK_INT >= 21) {
            Bundle params = new Bundle();
            params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            tts.speak(text, flush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, params, id);
        } else {
            tts.speak(text, flush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null);
        }
    }

    public void stop() {
        if (tts != null) tts.stop();
        abandonFocus();
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        ready = false;
        abandonFocus();
    }

    private void requestFocus() {
        if (Build.VERSION.SDK_INT >= 26) {
            if (focusRequest == null) {
                focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build())
                        .build();
            }
            audioManager.requestAudioFocus(focusRequest);
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        }
    }

    private void abandonFocus() {
        if (Build.VERSION.SDK_INT >= 26) {
            if (focusRequest != null) audioManager.abandonAudioFocusRequest(focusRequest);
        } else {
            audioManager.abandonAudioFocus(null);
        }
    }
}
