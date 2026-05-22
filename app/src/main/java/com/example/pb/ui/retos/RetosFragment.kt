package com.example.pb.ui.retos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pb.R
import com.example.pb.databinding.FragmentRetosBinding
import com.example.pb.model.Reto
import com.example.pb.viewmodel.RetosViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RetosFragment : Fragment() {

    private var _binding: FragmentRetosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RetosViewModel by viewModels()

    private val adapter by lazy {
        RetosAdapter(onDeleteClick = { reto -> confirmarEliminar(reto) })
    }

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

        binding.rvRetos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRetos.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allRetos.collectLatest { retos ->
                adapter.submitList(retos)
                binding.tvEmpty.visibility = if (retos.isEmpty()) View.VISIBLE else View.GONE
                binding.rvRetos.visibility = if (retos.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        binding.fabAdd.setOnClickListener { /* HU 7.0 - otro integrante */ }
    }

    // HU 9.0 — Eliminar Reto
    private fun confirmarEliminar(reto: Reto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.challenges_delete)
            .setMessage(R.string.challenges_delete_confirm)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_delete) { _, _ ->
                viewModel.delete(reto)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
