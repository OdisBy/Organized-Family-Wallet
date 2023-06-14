package com.ruliam.organizedfw.features.login.signup_screen

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ruliam.organizedfw.core.data.util.SignUpResult
import com.ruliam.organizedfw.features.login.databinding.FragmentSignUpBinding
import com.ruliam.organizedfw.features.login.utils.InputResource
import com.ruliam.organizedfw.features.login.utils.LoginUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SignUpViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.INVISIBLE

        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        binding.emailSignIn.setOnClickListener {
            if(checkAll()){
                val email = binding.emailInput.editText?.text.toString()
                val password = binding.passwordInput.editText?.text.toString()
                viewModel.registerUser(email, password)
            }
        }

        lifecycleScope.launch {
            viewModel.signInState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { signInState ->
                    when(signInState){
                        is SignUpResult.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is SignUpResult.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            navigateToInformation()
                        }
                        is SignUpResult.AlreadyHasAccount -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.emailInput.error = signInState.message
                        }
                        is SignUpResult.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            Snackbar.make(
                                binding.root,
                                signInState.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                }
        }
    }

    private fun navigateToInformation() {
        val request = NavDeepLinkRequest.Builder
            .fromUri("organized-app://com.odisby.organizedfw/register_information".toUri())
            .build()
        findNavController().navigate(request)
    }

    private fun checkAll(): Boolean {
        val email = binding.emailInput.editText?.text.toString()
        val password = binding.passwordInput.editText?.text.toString()
        val confirmPassword = binding.confirmPasswordInput.editText?.text.toString()

        when (val emailResult = LoginUtil.validateEmailInput(email)) {
            is InputResource.Success -> {
                binding.emailInput.error = null
            }
            is InputResource.Error -> {
                binding.emailInput.error = emailResult.message
                return false
            }
        }
        when (val passwordResult = LoginUtil.validatePasswordInput(password)) {
            is InputResource.Success -> {
                binding.passwordInput.error = null
            }
            is InputResource.Error -> {
                binding.passwordInput.error = passwordResult.message
                return false
            }
        }
        when (val confirmPassResult = LoginUtil.validateConfirmPasswordInput(password, confirmPassword)) {
            is InputResource.Success -> {
                binding.confirmPasswordInput.error = null
            }
            is InputResource.Error -> {
                binding.confirmPasswordInput.error = confirmPassResult.message
                return false
            }
        }
        return true
    }

    companion object {
        private const val TAG = "SignUpFragment"
    }
}