package com.example.dipl.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.databinding.FragmentLoginBinding
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.viewmodel.LoginViewModel


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentLoginBinding == null")

    private val viewModel: LoginViewModel by viewModels() {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnRegistration.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        binding.btnEnter.setOnClickListener {
            performAuthentication()
        }

        getResultEnter()

        return binding.root
    }

    private fun performAuthentication() {
        val phone = binding.phoneEnter.text.toString()
        val password = binding.passwordEnter.text.toString()

        if (validation(phone, password)) return

        viewModel.auth(phone, password)
    }

    private fun validation(phone: String, password: String): Boolean {
        if (phone.isBlank()) {
            Toast.makeText(context, "Введите номер телефона", Toast.LENGTH_SHORT).show()
            return true
        }

        if (password.isBlank()) {
            Toast.makeText(context, "Введите пароль", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    private fun getResultEnter() {
        viewModel.enterResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginViewModel.LoginResult.Success -> {
                    navigateToHomeScreen()
                    //PrefManager.setLoggedInState(requireContext(), true)
                }
                is LoginViewModel.LoginResult.Error -> {
                    val message = result.message
                    showError(message)
                }
            }
        }
    }

    private fun navigateToHomeScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
    }



    private fun showError(message: String) {
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}