package com.example.pb.viewmodel

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pb.R

class AudioViewModel(application: Application) : AndroidViewModel(application) {

    private var bgMediaPlayer: MediaPlayer? = null
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

    override fun onCleared() {
        super.onCleared()
        bgMediaPlayer?.release()
        bgMediaPlayer = null
    }
}
