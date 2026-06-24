package com.example.pb.ui.retos

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import coil.load
import com.example.pb.databinding.DialogRetoAleatorioBinding

class DialogRetoAleatorio : DialogFragment() {

    private var _binding: DialogRetoAleatorioBinding? = null
    private val binding get() = _binding!!

    private var onDismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRetoAleatorioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retoTexto = arguments?.getString(ARG_RETO) ?: ""
        val pokemonName = arguments?.getString(ARG_POKEMON_NAME) ?: ""
        val pokemonUrl = arguments?.getString(ARG_POKEMON_URL) ?: ""

        binding.tvReto.text = retoTexto
        binding.tvPokemonName.text = pokemonName
        binding.ivPokemon.load(pokemonUrl)

        binding.btnCerrar.setOnClickListener {
            onDismissListener?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DialogRetoAleatorio"
        private const val ARG_RETO = "reto_texto"
        private const val ARG_POKEMON_NAME = "pokemon_name"
        private const val ARG_POKEMON_URL = "pokemon_url"

        fun newInstance(reto: String, pokemonName: String, pokemonUrl: String) =
            DialogRetoAleatorio().apply {
                arguments = Bundle().apply {
                    putString(ARG_RETO, reto)
                    putString(ARG_POKEMON_NAME, pokemonName)
                    putString(ARG_POKEMON_URL, pokemonUrl)
                }
            }
    }
}
