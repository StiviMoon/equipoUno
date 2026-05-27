package historia.numeroOcho.ui.dialog


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import historia.numeroOcho.data.database.RetoRepository
import historia.numeroOcho.data.model.Reto
import historia.numeroOcho.R

class EditarRetoDialog : DialogFragment() {

    private lateinit var retoRepository: RetoRepository
    private lateinit var listener: EditarRetoDialogListener
    private var retoId: Long = -1

    companion object {
        private const val ARG_RETO_ID = "reto_id"

        fun newInstance(retoId: Long): EditarRetoDialog {
            val fragment = EditarRetoDialog()
            val args = Bundle()
            args.putLong(ARG_RETO_ID, retoId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as EditarRetoDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context debe implementar EditarRetoDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retoRepository = RetoRepository(requireContext())
        retoId = arguments?.getLong(ARG_RETO_ID) ?: -1

        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_editar_reto, null)

        val tvTitulo = view.findViewById<TextView>(R.id.tvTituloDialog)
        val etDescripcion = view.findViewById<EditText>(R.id.etDescripcionReto)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        // Cargar datos actuales del reto
        val reto = retoRepository.obtenerRetoPorId(retoId)
        if (reto != null) {
            etDescripcion.setText(reto.descripcion)
            etDescripcion.setSelection(reto.descripcion.length)
        } else {
            // Si no existe el reto, cerrar diálogo
            dismiss()
            return builder.create()
        }

        btnCancelar.setOnClickListener {
            listener.onEdicionCancelada()
            dismiss()
        }

        btnGuardar.setOnClickListener {
            val nuevaDescripcion = etDescripcion.text.toString().trim()
            if (validarDescripcion(nuevaDescripcion)) {
                val retoActualizado = Reto(retoId, nuevaDescripcion)
                val exito = retoRepository.actualizarReto(retoActualizado)
                if (exito) {
                    listener.onRetoActualizado(retoId, nuevaDescripcion)
                    dismiss()
                } else {
                    mostrarError("Error al guardar el reto")
                }
            } else {
                etDescripcion.error = "La descripción no puede estar vacía"
            }
        }

        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false) // No cerrar al tocar fuera
        return dialog
    }

    private fun validarDescripcion(descripcion: String): Boolean {
        return descripcion.isNotEmpty() && descripcion.length <= 200
    }

    private fun mostrarError(mensaje: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(mensaje)
            .setPositiveButton("OK", null)
            .show()
    }
}