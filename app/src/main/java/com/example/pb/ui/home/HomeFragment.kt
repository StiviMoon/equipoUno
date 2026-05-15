package com.example.pb.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pb.R
import com.example.pb.databinding.FragmentHomeBinding
import com.example.pb.utils.startAnim
import com.example.pb.viewmodel.AudioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val audioViewModel: AudioViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioViewModel.startBgMusic()

        binding.btnPress.startAnim(R.anim.btn_blink)

        binding.btnPress.setOnClickListener { spinBottle() }

        binding.customToolbar.setOnRateClick { /* HU 4.0 */ }
        binding.customToolbar.setOnAudioClick { audioViewModel.toggleAudio() }
        binding.customToolbar.setOnInstructionsClick {
            findNavController().navigate(R.id.action_home_to_instrucciones)
        }
        binding.customToolbar.setOnChallengesClick {
            findNavController().navigate(R.id.action_home_to_retos)
        }
        binding.customToolbar.setOnShareClick { /* HU 10 */ }

        audioViewModel.isAudioOn.observe(viewLifecycleOwner) { isOn ->
            binding.customToolbar.updateAudioIcon(isOn)
        }
    }

    private fun spinBottle() {
        binding.btnPress.clearAnimation()
        binding.btnPress.visibility = View.INVISIBLE
        audioViewModel.pauseTemporarily()

        binding.ivBottle.startAnim(R.anim.bottle_spin)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000L)
            binding.ivBottle.clearAnimation()
            startCountdown()
        }
    }

    private fun startCountdown() {
        binding.tvCountdown.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 3 downTo 1) {
                binding.tvCountdown.text = i.toString()
                delay(1000L)
            }
            binding.tvCountdown.visibility = View.INVISIBLE
            resetButton()
            audioViewModel.resumeIfEnabled()
        }
    }

    private fun resetButton() {
        binding.btnPress.visibility = View.VISIBLE
        binding.btnPress.startAnim(R.anim.btn_blink)
    }

    override fun onPause() {
        super.onPause()
        audioViewModel.pauseBgMusic()
    }

    override fun onResume() {
        super.onResume()
        audioViewModel.resumeIfEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
