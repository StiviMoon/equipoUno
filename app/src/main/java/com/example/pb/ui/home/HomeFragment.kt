package com.example.pb.ui.home

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
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
import androidx.core.net.toUri

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

        binding.customToolbar.setOnRateClick {
            abrirCalificacion()
        }

        binding.customToolbar.setOnAudioClick { audioViewModel.toggleAudio() }
        binding.customToolbar.setOnInstructionsClick {
            findNavController().navigate(R.id.action_home_to_instrucciones)
        }
        binding.customToolbar.setOnChallengesClick {
            findNavController().navigate(R.id.action_home_to_retos)
        }
        binding.customToolbar.setOnShareClick { compartirApp() }

        audioViewModel.isAudioOn.observe(viewLifecycleOwner) { isOn ->
            binding.customToolbar.updateAudioIcon(isOn)
        }
    }

    private fun spinBottle() {
        binding.btnPress.clearAnimation()
        binding.btnPress.visibility = View.INVISIBLE
        audioViewModel.pauseTemporarily()

        viewLifecycleOwner.lifecycleScope.launch {
            // Fase 1: contador 3, 2, 1, 0
            binding.tvCountdown.visibility = View.VISIBLE
            for (i in 3 downTo 0) {
                binding.tvCountdown.text = i.toString()
                delay(800L)
            }
            binding.tvCountdown.visibility = View.INVISIBLE

            // Fase 2: girar botella
            launchSpinAnimation()

            delay(2600L)
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
    private fun abrirCalificacion() {
        val intent = android.content.Intent(
            android.content.Intent.ACTION_VIEW,
            getString(R.string.rate_app_url).toUri()
        )
        startActivity(intent)
    }

    private fun compartirApp() {
        val mensaje = getString(R.string.share_app_mensaje)
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, mensaje)
        }
        startActivity(android.content.Intent.createChooser(intent, getString(R.string.share_chooser_title)))
    }

    private fun launchSpinAnimation() {
        val randomAngle = (0..359).random().toFloat()
        val current = binding.ivBottle.rotation % 360f
        val target = current + (360f * 5) + randomAngle

        binding.ivBottle.rotation = current
        binding.ivBottle.animate()
            .rotation(target)
            .setDuration(2500)
            .setInterpolator(DecelerateInterpolator())
            .start()
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
