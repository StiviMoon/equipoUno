package com.example.pb.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.pb.R
import com.example.pb.databinding.FragmentHomeBinding
import com.example.pb.ui.retos.DialogRetoAleatorio
import com.example.pb.utils.startAnim
import com.example.pb.viewmodel.AudioViewModel
import com.example.pb.viewmodel.RetosViewModel
import com.example.pb.viewmodel.PokemonViewModel
import com.example.pb.viewmodel.RandomRetoState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val audioViewModel: AudioViewModel by activityViewModels()
    private val retosViewModel: RetosViewModel by viewModels()
    private val pokemonViewModel: PokemonViewModel by viewModels()

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

        audioViewModel.resumeIfEnabled()
        binding.btnPress.startAnim(R.anim.btn_blink)
        binding.btnPress.setOnClickListener { spinBottle() }

        binding.customToolbar.setOnRateClick { abrirCalificacion() }
        binding.customToolbar.setOnAudioClick { audioViewModel.toggleAudio() }
        binding.customToolbar.setOnInstructionsClick {
            findNavController().navigate(R.id.action_home_to_instrucciones)
        }
        binding.customToolbar.setOnChallengesClick {
            findNavController().navigate(R.id.action_home_to_retos)
        }
        binding.customToolbar.setOnShareClick { compartirApp() }
        binding.customToolbar.setOnLogoutClick { cerrarSesion() }

        audioViewModel.isAudioOn.observe(viewLifecycleOwner) { isOn ->
            binding.customToolbar.updateAudioIcon(isOn)
        }
    }

    private fun cerrarSesion() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        findNavController().navigate(R.id.action_home_to_login)
    }

    private fun spinBottle() {
        binding.btnPress.clearAnimation()
        binding.btnPress.visibility = View.INVISIBLE
        audioViewModel.pauseTemporarily()

        // Disparar carga asíncrona de datos desde los ViewModels
        retosViewModel.fetchRandomReto()
        pokemonViewModel.loadRandomPokemon()

        viewLifecycleOwner.lifecycleScope.launch {
            // 1. Girar botella con sonido (HU11 C1, C2)
            audioViewModel.playSpinSound()
            launchSpinAnimation()
            delay(2500L)
            audioViewModel.stopSpinSound()

            // 2. Countdown DESPUÉS de que la botella se detiene (HU11 C5)
            binding.tvCountdown.visibility = View.VISIBLE
            for (i in 3 downTo 0) {
                binding.tvCountdown.text = i.toString()
                delay(800L)
            }
            binding.tvCountdown.visibility = View.INVISIBLE

            // Esperar a que terminen de cargar si es necesario
            var retoState = retosViewModel.randomRetoState.value
            var pokemonState = pokemonViewModel.pokemonState.value

            while (retoState is RandomRetoState.Loading || retoState is RandomRetoState.Idle ||
                pokemonState is PokemonViewModel.PokemonState.Loading || pokemonState is PokemonViewModel.PokemonState.Idle) {
                delay(100L)
                retoState = retosViewModel.randomRetoState.value
                pokemonState = pokemonViewModel.pokemonState.value
            }

            val finalRetoText = when (retoState) {
                is RandomRetoState.Success -> retoState.reto.descripcion
                is RandomRetoState.Empty -> "No existen retos disponibles."
                is RandomRetoState.Error -> "No fue posible obtener los retos."
                else -> "No existen retos disponibles."
            }

            var finalPokemonName = ""
            var finalPokemonUrl = ""

            when (pokemonState) {
                is PokemonViewModel.PokemonState.Success -> {
                    finalPokemonName = pokemonState.pokemon.name
                    finalPokemonUrl = pokemonState.pokemon.imageUrl
                }
                is PokemonViewModel.PokemonState.Error -> {
                    // Si no hay conexión o falla la API, mostramos error en un Toast y dejamos la imagen vacía/placeholder
                    android.widget.Toast.makeText(
                        requireContext(),
                        "No fue posible obtener la imagen. Verifique su conexión a Internet.",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }

            // 3. Mostrar reto
            mostrarRetoAleatorio(
                retoTexto = finalRetoText,
                pokemonName = finalPokemonName,
                pokemonUrl = finalPokemonUrl
            )
        }
    }

    // Animación de giro con ángulo aleatorio y desaceleración
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

    private fun mostrarRetoAleatorio(retoTexto: String, pokemonName: String, pokemonUrl: String) {
        try {
            val dialog = DialogRetoAleatorio.newInstance(retoTexto, pokemonName, pokemonUrl)
            dialog.setOnDismissListener {
                audioViewModel.stopRetoRevealSound()
                resetButton()
                audioViewModel.resumeIfEnabled()
            }
            dialog.show(childFragmentManager, DialogRetoAleatorio.TAG)
            audioViewModel.playRetoRevealSound()
        } catch (e: Exception) {
            resetButton()
            audioViewModel.resumeIfEnabled()
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
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_app_mensaje))
        }
        startActivity(android.content.Intent.createChooser(intent, getString(R.string.share_chooser_title)))
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
