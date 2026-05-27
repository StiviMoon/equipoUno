package com.example.pb.ui.retos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.pb.R
import com.example.pb.data.AppDatabase
import com.example.pb.databinding.FragmentRetosBinding
import com.example.pb.model.Reto
import com.example.pb.repository.RetoRepository
import com.example.pb.viewmodel.RetosUiState
import com.example.pb.viewmodel.RetosViewModel
import com.example.pb.viewmodel.RetosViewModelFactory
import kotlinx.coroutines.launch

class RetosFragment : Fragment() {

    private var _binding: FragmentRetosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RetosViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).retoDao()
        RetosViewModelFactory(RetoRepository(dao))
    }

    private lateinit var adapter: RetosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRetosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeRetos()
    }

    private fun setupRecyclerView() {
        adapter = RetosAdapter(
            onEdit   = { reto -> EditarRetoDialog.newInstance(reto).show(childFragmentManager, EditarRetoDialog.TAG) },
            onDelete = { reto -> confirmarEliminar(reto) } // HU 9.0: DeleteRetoDialog
        )
        binding.rvRetos.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.rvRetos.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            AgregarRetoDialog().show(childFragmentManager, AgregarRetoDialog.TAG)
        }
    }

    private fun observeRetos() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is RetosUiState.Success -> {
                            adapter.submitList(state.retos)
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvRetos.visibility = View.VISIBLE
                        }
                        is RetosUiState.Empty -> {
                            adapter.submitList(emptyList())
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvRetos.visibility = View.GONE
                        }
                        is RetosUiState.Loading -> {
                            binding.tvEmpty.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    // HU 9.0 — Eliminar Reto
    private fun confirmarEliminar(reto: Reto) {
        val dialog = android.app.Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_delete_reto)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(false) // Criterio 6

        val tvRetoDescription = dialog.findViewById<android.widget.TextView>(R.id.tvRetoDescription)
        val btnNo = dialog.findViewById<android.widget.TextView>(R.id.btnNo)
        val btnSi = dialog.findViewById<android.widget.TextView>(R.id.btnSi)

        tvRetoDescription.text = reto.descripcion

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        btnSi.setOnClickListener {
            viewModel.eliminarReto(reto)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
