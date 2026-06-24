package com.example.pb.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pb.R
import com.example.pb.databinding.FragmentLoginBinding
import com.example.pb.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun validateEmail(): Boolean {

        val email = binding.etEmail.text.toString().trim()

        return when {

            email.isEmpty() -> {
                binding.tilEmail.error = "El correo es obligatorio"
                false
            }

            email.length > 40 -> {
                binding.tilEmail.error = "Máximo 40 caracteres"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Correo inválido"
                false
            }

            else -> {
                binding.tilEmail.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {

        val password = binding.etPassword.text.toString()

        return if (Regex("^\\d{6,10}$").matches(password)) {

            binding.tilPassword.error = null
            true

        } else {

            binding.tilPassword.error =
                "Debe contener entre 6 y 10 números"

            false
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        authViewModel.loginWithEmail(email, password)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.etEmail.doAfterTextChanged {
            validateEmail()
        }

        binding.etPassword.doAfterTextChanged {
            validatePassword()
        }

        binding.btnLogin.setOnClickListener {
            val emailOk = validateEmail()
            val passwordOk = validatePassword()

            if (emailOk && passwordOk) {
                loginUser()
            }
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(
                R.id.action_login_to_register
            )
        }

        observeAuthState()
    }

    private fun observeAuthState() {
        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(
                        R.id.action_login_to_home
                    )
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                is AuthViewModel.AuthState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}