package com.example.pb.ui.register

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
import com.example.pb.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentRegisterBinding.inflate(
                inflater,
                container,
                false
            )

        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    private fun validateEmail(): Boolean {

        val email =
            binding.etEmailRegistro.text.toString().trim()

        return when {

            email.isEmpty() -> {
                binding.tilEmailRegistro.error =
                    "El correo es obligatorio"
                false
            }

            email.length > 40 -> {
                binding.tilEmailRegistro.error =
                    "Máximo 40 caracteres"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmailRegistro.error =
                    "Correo inválido"
                false
            }

            else -> {
                binding.tilEmailRegistro.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {

        val password =
            binding.etPasswordRegistro.text.toString()

        return if (
            Regex("^\\d{6,10}$").matches(password)
        ) {

            binding.tilPasswordRegistro.error = null
            true

        } else {

            binding.tilPasswordRegistro.error =
                "Debe contener entre 6 y 10 números"

            false
        }
    }

    private fun validateConfirmPassword(): Boolean {

        return if (
            binding.etPasswordRegistro.text.toString() ==
            binding.etConfirmPassword.text.toString()
        ) {

            binding.tilConfirmPassword.error = null
            true

        } else {

            binding.tilConfirmPassword.error =
                "Las contraseñas no coinciden"

            false
        }
    }

    private fun registerUser() {

        val email =
            binding.etEmailRegistro.text.toString().trim()

        val password =
            binding.etPasswordRegistro.text.toString()

        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener {

            if (it.isSuccessful) {

                Toast.makeText(
                    requireContext(),
                    "Cuenta creada correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(
                    R.id.action_register_to_home
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

        binding.etEmailRegistro.doAfterTextChanged {
            validateEmail()
        }

        binding.etPasswordRegistro.doAfterTextChanged {
            validatePassword()
            validateConfirmPassword()
        }

        binding.etConfirmPassword.doAfterTextChanged {
            validateConfirmPassword()
        }

        binding.btnCrearCuenta.setOnClickListener {

            val emailOk = validateEmail()
            val passwordOk = validatePassword()
            val confirmOk = validateConfirmPassword()

            if (
                emailOk &&
                passwordOk &&
                confirmOk
            ) {

                registerUser()
            }
        }

        binding.btnVolverLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}