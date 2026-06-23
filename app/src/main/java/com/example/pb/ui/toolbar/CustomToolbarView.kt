package com.example.pb.ui.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils as AndroidAnimationUtils
import android.widget.LinearLayout
import com.example.pb.R
import com.example.pb.databinding.ViewCustomToolbarBinding

class CustomToolbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomToolbarBinding =
        ViewCustomToolbarBinding.inflate(LayoutInflater.from(context), this)

    private val touchAnim by lazy {
        AndroidAnimationUtils.loadAnimation(context, R.anim.touch_scale)
    }

    private var onRateClick: (() -> Unit)? = null
    private var onAudioClick: (() -> Unit)? = null
    private var onInstructionsClick: (() -> Unit)? = null
    private var onChallengesClick: (() -> Unit)? = null
    private var onShareClick: (() -> Unit)? = null
    private var onLogoutClick: (() -> Unit)? = null

    init {
        setupClicks()
    }

    private fun setupClicks() {
        binding.btnRate.setOnClickListener {
            it.startAnimation(touchAnim)
            it.postDelayed({ onRateClick?.invoke() }, 150)
        }
        binding.btnAudio.setOnClickListener {
            it.startAnimation(touchAnim)
            it.postDelayed({ onAudioClick?.invoke() }, 150)
        }
        binding.btnInstructions.setOnClickListener {
            it.startAnimation(touchAnim)
            it.postDelayed({ onInstructionsClick?.invoke() }, 150)
        }
        binding.btnChallenges.setOnClickListener {
            it.startAnimation(touchAnim)
            it.postDelayed({ onChallengesClick?.invoke() }, 150)
        }
        binding.btnShare.setOnClickListener {
            it.startAnimation(touchAnim)
            it.postDelayed({ onShareClick?.invoke() }, 150)
        }
        binding.btnLogout.setOnClickListener {
            it.startAnimation(touchAnim)
            it.postDelayed({ onLogoutClick?.invoke() }, 150)
        }
    }

    fun updateAudioIcon(isOn: Boolean) {
        val iconRes = if (isOn) R.drawable.ic_audio_on else R.drawable.ic_audio_off
        binding.btnAudio.setImageResource(iconRes)
    }

    fun setOnRateClick(block: () -> Unit) { onRateClick = block }
    fun setOnAudioClick(block: () -> Unit) { onAudioClick = block }
    fun setOnInstructionsClick(block: () -> Unit) { onInstructionsClick = block }
    fun setOnChallengesClick(block: () -> Unit) { onChallengesClick = block }
    fun setOnShareClick(block: () -> Unit) { onShareClick = block }
    fun setOnLogoutClick(block: () -> Unit) { onLogoutClick = block }
}
