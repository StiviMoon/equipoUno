package com.example.pb.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pb.R
import com.example.pb.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

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

        val email =
            binding.etEmail.text.toString().trim()

        val password =
            binding.etPassword.text.toString()

        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener {

            if (it.isSuccessful) {

                Toast.makeText(
                    requireContext(),
                    "Bienvenido",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(
                    R.id.action_login_to_home
                )

            } else {

                Toast.makeText(
                    requireContext(),
                    it.exception?.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}