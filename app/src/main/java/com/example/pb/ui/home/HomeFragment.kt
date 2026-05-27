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
import com.example.pb.data.AppDatabase
import com.example.pb.repository.PokemonRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val audioViewModel: AudioViewModel by activityViewModels()

    companion object {
        private const val TAG = "HomeFragment"
    }

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
            showRandomRetoDialog()
        }
    }

    private fun showRandomRetoDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1. Fetch random challenge from local DB (fast)
                val dao = AppDatabase.getInstance(requireContext()).retoDao()
                val randomReto = dao.getRandomReto()
                val retoText = randomReto?.descripcion ?: "No hay retos disponibles."
                Log.d(TAG, "Reto obtenido: $retoText")

                // 2. Create and show the dialog IMMEDIATELY (don't wait for network)
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_random_reto)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(false) // Criterio 6: solo se cierra con "Cerrar"

                val tvReto = dialog.findViewById<TextView>(R.id.tvReto)
                val ivPokemon = dialog.findViewById<ImageView>(R.id.ivPokemon)
                val btnCerrar = dialog.findViewById<Button>(R.id.btnCerrar)

                tvReto.text = retoText // Criterio 3

                btnCerrar.setOnClickListener {
                    dialog.dismiss()
                    resetButton()
                    audioViewModel.resumeIfEnabled()
                }

                dialog.show() // Show dialog right away!
                Log.d(TAG, "Diálogo mostrado exitosamente")

                // 3. Load Pokemon image ASYNCHRONOUSLY (after dialog is already visible)
                launch {
                    try {
                        val repository = PokemonRepository()
                        val imageUrl = repository.getRandomPokemonImageUrl()
                        Log.d(TAG, "Pokemon image URL: $imageUrl")

                        imageUrl?.let { urlString ->
                            val bitmap = withContext(Dispatchers.IO) {
                                val url = URL(urlString)
                                val connection = url.openConnection() as HttpURLConnection
                                connection.connectTimeout = 5000
                                connection.readTimeout = 5000
                                connection.doInput = true
                                connection.connect()
                                val input = connection.inputStream
                                BitmapFactory.decodeStream(input)
                            }
                            if (dialog.isShowing) {
                                ivPokemon.setImageBitmap(bitmap)
                                Log.d(TAG, "Imagen de Pokémon cargada")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error cargando imagen de Pokémon", e)
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error mostrando diálogo de reto", e)
                // Even if something fails, make sure the button resets
                resetButton()
                audioViewModel.resumeIfEnabled()
            }
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
