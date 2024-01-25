package com.example.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.notes.databinding.FragmentLoginBinding
import com.example.notes.models.UserRequest
import com.example.notes.utils.NetworkResult
import com.example.notes.utils.TokenManager
import com.example.notes.utils.ValidateUserInput
import com.example.notes.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class loginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var validateUserInput: ValidateUserInput

    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        if (tokenManager.getToken() != null) {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
        validateUserInput = ValidateUserInput()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUp.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnLogin.setOnClickListener {
            val validation = validateUserInput()
            if (validation.first) {
                authViewModel.loginUser(userRequest())
            } else {
                binding.txtError.text = validation.second
            }
        }
        bindObserver()
    }

    // User Inputs
    private fun userRequest(): UserRequest {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        return UserRequest("", email, password)
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest = userRequest()
        return validateUserInput.validate(userRequest.username, userRequest.email, userRequest.password, true)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindObserver() {
        authViewModel.userResponseLiveData.observe(
            viewLifecycleOwner,
            Observer {
                binding.progressBar.isVisible = false
                when (it) {
                    is NetworkResult.Success -> {
                        tokenManager.saveToken(it.data!!.token)
                        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                    }
                    is NetworkResult.Error -> {
                        binding.txtError.text = it.message
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                }
            }
        )
    }
}
