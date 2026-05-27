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
        val dialog = android.app.Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_delete_reto)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(false) // Criterio 6

        val tvRetoDescription = dialog.findViewById<android.widget.TextView>(R.id.tvRetoDescription)
        val btnNo = dialog.findViewById<android.widget.TextView>(R.id.btnNo)
        val btnSi = dialog.findViewById<android.widget.TextView>(R.id.btnSi)

        tvRetoDescription.text = reto.texto // Criterio 3

        // Criterio 4
        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        // Criterio 5
        btnSi.setOnClickListener {
            viewModel.delete(reto)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
