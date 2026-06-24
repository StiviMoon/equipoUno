package com.example.pb.viewmodel

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pb.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private var bgMediaPlayer: MediaPlayer? = null
    private var spinMediaPlayer: MediaPlayer? = null
    private var retoRevealPlayer: MediaPlayer? = null
    private val _isAudioOn = MutableLiveData(true)
    val isAudioOn: LiveData<Boolean> = _isAudioOn

    init {
        initBgPlayer()
    }

    private fun initBgPlayer() {
        val res = getApplication<Application>().resources
        val rawId = res.getIdentifier("bg_music", "raw", getApplication<Application>().packageName)
        if (rawId != 0) {
            bgMediaPlayer = MediaPlayer.create(getApplication(), rawId)?.apply {
                isLooping = true
            }
        }
    }

    fun startBgMusic() {
        if (_isAudioOn.value == true) bgMediaPlayer?.start()
    }

    fun pauseBgMusic() {
        bgMediaPlayer?.pause()
    }

    fun toggleAudio() {
        val on = _isAudioOn.value ?: true
        if (on) {
            bgMediaPlayer?.pause()
            _isAudioOn.value = false
        } else {
            bgMediaPlayer?.start()
            _isAudioOn.value = true
        }
    }

    fun pauseTemporarily() {
        bgMediaPlayer?.pause()
    }

    fun resumeIfEnabled() {
        if (_isAudioOn.value == true) bgMediaPlayer?.start()
    }

    // HU 11.0 C2: sonido de botella girando
    fun playSpinSound() {
        try {
            val rawId = getApplication<Application>().resources
                .getIdentifier("spin_sound", "raw", getApplication<Application>().packageName)
            if (rawId != 0) {
                spinMediaPlayer?.release()
                spinMediaPlayer = MediaPlayer.create(getApplication(), rawId)?.apply { start() }
            }
        } catch (_: Exception) { }
    }

    fun stopSpinSound() {
        spinMediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        spinMediaPlayer = null
    }

    fun playRetoRevealSound() {
        try {
            val rawId = getApplication<Application>().resources
                .getIdentifier("reto_reveal", "raw", getApplication<Application>().packageName)
            if (rawId != 0) {
                retoRevealPlayer?.release()
                retoRevealPlayer = MediaPlayer.create(getApplication(), rawId)?.apply { start() }
            }
        } catch (_: Exception) { }
    }

    fun stopRetoRevealSound() {
        retoRevealPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        retoRevealPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        bgMediaPlayer?.release()
        bgMediaPlayer = null
        spinMediaPlayer?.release()
        spinMediaPlayer = null
        retoRevealPlayer?.release()
        retoRevealPlayer = null
    }
}
