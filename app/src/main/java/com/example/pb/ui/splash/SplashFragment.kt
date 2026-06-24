package com.example.pb.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.pb.utils.startAnim
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pb.R
import com.example.pb.databinding.FragmentSplashBinding
import com.example.pb.viewmodel.AudioViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val audioViewModel: AudioViewModel by activityViewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioViewModel.startBgMusic()
        binding.ivBottleAnim.startAnim(R.anim.bottle_pulse)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(5000L)

            val currentUser = firebaseAuth.currentUser

            if (currentUser != null) {

                findNavController().navigate(
                    R.id.action_splash_to_home
                )

            } else {

                findNavController().navigate(
                    R.id.action_splash_to_login
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
