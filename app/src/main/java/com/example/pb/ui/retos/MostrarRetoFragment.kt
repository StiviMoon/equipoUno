package com.example.pb.ui.retos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pb.R
import com.example.pb.databinding.FragmentMostrarRetoBinding
import com.example.pb.viewmodel.RetosViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MostrarRetoFragment : Fragment() {

    private var _binding: FragmentMostrarRetoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RetosViewModel by viewModels {
        val dao = com.example.pb.data.AppDatabase.getInstance(requireContext()).retoDao()
        com.example.pb.viewmodel.RetosViewModelFactory(com.example.pb.repository.RetoRepository(dao))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMostrarRetoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // HU 12 — Mostrar un reto aleatorio de la lista
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is com.example.pb.viewmodel.RetosUiState.Success -> {
                        val retos = state.retos
                        if (retos.isNotEmpty()) {
                            binding.tvRetoTexto.text = retos.random().descripcion
                        }
                    }
                    is com.example.pb.viewmodel.RetosUiState.Empty -> {
                        binding.tvRetoTexto.text = getString(R.string.challenges_empty)
                    }
                    is com.example.pb.viewmodel.RetosUiState.Loading -> { }
                }
            }
        }

        binding.btnCerrar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
