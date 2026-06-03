package com.example.pb.ui.retos

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.pb.data.AppDatabase
import com.example.pb.databinding.DialogAgregarRetoBinding
import com.example.pb.repository.RetoRepository
import com.example.pb.viewmodel.RetosViewModel
import com.example.pb.viewmodel.RetosViewModelFactory

class AgregarRetoDialog : DialogFragment() {

    private var _binding: DialogAgregarRetoBinding? = null
    private val binding get() = _binding!!


    private val viewModel: RetosViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = {
            val dao = AppDatabase.getInstance(requireContext()).retoDao()
            RetosViewModelFactory(RetoRepository(dao))
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAgregarRetoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Estado inicial correcto: disabled + gris
        binding.btnGuardar.isEnabled = false
        binding.btnGuardar.setTextColor(requireContext().getColor(android.R.color.darker_gray))

        // Focus + teclado al abrir
        binding.etReto.requestFocus()
        binding.etReto.post {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etReto, InputMethodManager.SHOW_IMPLICIT)
        }

        setupTextWatcher()
        setupBotones()
    }


    private fun setupTextWatcher() {
        binding.etReto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val tieneTexto = !s.isNullOrBlank()
                binding.btnGuardar.isEnabled = tieneTexto
                binding.btnGuardar.setTextColor(
                    if (tieneTexto)
                        requireContext().getColor(com.example.pb.R.color.orange)
                    else
                        requireContext().getColor(android.R.color.darker_gray)
                )
            }
        })
    }

    private fun setupBotones() {

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }


        binding.btnGuardar.setOnClickListener {
            val descripcion = binding.etReto.text.toString().trim()
            if (descripcion.isNotBlank()) {
                viewModel.insertarReto(descripcion)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AgregarRetoDialog"
    }
}