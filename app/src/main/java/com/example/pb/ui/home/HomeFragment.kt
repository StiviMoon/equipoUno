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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.pb.R
import com.example.pb.data.AppDatabase
import com.example.pb.databinding.FragmentHomeBinding
import com.example.pb.repository.PokemonRepository
import com.example.pb.utils.startAnim
import com.example.pb.viewmodel.AudioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val audioViewModel: AudioViewModel by activityViewModels()

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

        viewLifecycleOwner.lifecycleScope.launch {
            // Precargar reto de BD mientras gira+countdown (~5.7s)
            val dao = AppDatabase.getInstance(requireContext()).retoDao()
            val retoDeferred = async(Dispatchers.IO) { dao.getRandomReto() }
            val pokemonUrl = PokemonRepository().getRandomPokemonImageUrl()

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

            // 3. Mostrar reto — Coil carga imagen desde URL
            mostrarRetoAleatorio(
                retoTexto = retoDeferred.await()?.descripcion ?: "No hay retos disponibles.",
                pokemonUrl = pokemonUrl
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

    private fun mostrarRetoAleatorio(retoTexto: String, pokemonUrl: String) {
        try {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_random_reto)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)

            dialog.findViewById<TextView>(R.id.tvReto).text = retoTexto
            dialog.findViewById<ImageView>(R.id.ivPokemon).load(pokemonUrl)

            dialog.findViewById<Button>(R.id.btnCerrar).setOnClickListener {
                audioViewModel.stopRetoRevealSound()
                dialog.dismiss()
                resetButton()
                audioViewModel.resumeIfEnabled()
            }
            dialog.show()
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
