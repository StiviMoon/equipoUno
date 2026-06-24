package com.example.pb.ui.instrucciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pb.R
import com.example.pb.databinding.FragmentInstruccionesBinding
import com.example.pb.viewmodel.AudioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstruccionesFragment : Fragment() {

    private var _binding: FragmentInstruccionesBinding? = null
    private val binding get() = _binding!!

    private val audioViewModel: AudioViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstruccionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioViewModel.pauseBgMusic()

        binding.toolbarInstrucciones.setNavigationOnClickListener {
            audioViewModel.resumeIfEnabled()
            findNavController().navigateUp()
        }

        cargarAnimacionTrofeo()
    }

    private fun cargarAnimacionTrofeo() {
        val anim = android.view.animation.AnimationUtils.loadAnimation(
            requireContext(), R.anim.trofeo_scale
        )
        binding.ivTrofeo.startAnimation(anim)
    }

    override fun onStop() {
        super.onStop()
        audioViewModel.resumeIfEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}