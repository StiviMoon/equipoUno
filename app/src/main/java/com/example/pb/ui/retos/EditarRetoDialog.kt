package com.example.pb.ui.retos

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.pb.data.model.Reto
import com.example.pb.databinding.DialogEditarRetoBinding
import com.example.pb.viewmodel.RetosViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditarRetoDialog : DialogFragment() {

    private var _binding: DialogEditarRetoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RetosViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditarRetoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // Criterio 7: solo cierra con botones
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Criterio 3: poblar EditText con descripción actual de la BD
        val retoId = requireArguments().getString(ARG_ID, "")
        val descripcionActual = requireArguments().getString(ARG_DESCRIPCION, "")
        binding.etReto.setText(descripcionActual)
        binding.etReto.setSelection(descripcionActual.length)

        // Criterio 4: Cancelar
        binding.btnCancelar.setOnClickListener { dismiss() }

        // Criterios 5 y 6: Guardar siempre habilitado, guarda y actualiza lista
        binding.btnGuardar.setOnClickListener {
            val texto = binding.etReto.text.toString().trim()
            if (texto.isBlank()) {
                binding.etReto.error = "La descripción es obligatoria"
                return@setOnClickListener
            }
            viewModel.editarReto(Reto(id = retoId, descripcion = texto))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EditarRetoDialog"
        private const val ARG_ID = "reto_id"
        private const val ARG_DESCRIPCION = "reto_descripcion"

        fun newInstance(reto: Reto) = EditarRetoDialog().apply {
            arguments = bundleOf(
                ARG_ID to reto.id,
                ARG_DESCRIPCION to reto.descripcion
            )
        }
    }
}
