package com.ruliam.organizedfw.features.login.login_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ruliam.organizedfw.core.data.util.SignInResult
import com.ruliam.organizedfw.features.login.databinding.FragmentLoginBinding
import com.ruliam.organizedfw.features.login.utils.InputResource
import com.ruliam.organizedfw.features.login.utils.LoginUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        viewModel.isLogged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.INVISIBLE

        if(viewModel.startLogged){
            navigateToHomeFragment()
        }

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(com.ruliam.organizedfw.core.ui.R.id.action_login_to_sign_up)
        }

        binding.emailSignIn.setOnClickListener {
            if(checkAll()){
                val email = binding.emailInput.editText?.text.toString()
                val password = binding.passwordInput.editText?.text.toString()
                viewModel.loginUser(email, password)
            }
        }

        lifecycleScope.launch {
            viewModel.signInState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { signInState ->
                    clearErrors()
                    when(signInState){
                        is SignInResult.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is SignInResult.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            userLogged(signInState.userId, signInState.groupId)
                            navigateToHomeFragment()
                        }
                        is SignInResult.PasswordError -> {
                            binding.passwordInput.error = signInState.message
                            binding.progressBar.visibility = View.INVISIBLE
                        }
                        is SignInResult.EmailError -> {
                            binding.emailInput.error = signInState.message
                            binding.progressBar.visibility = View.INVISIBLE
                        }
                        is SignInResult.AccountNotCompleted -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            Snackbar.make(
                                binding.root,
                                signInState.message,
                                Snackbar.LENGTH_LONG
                            ).show()
                            navigateCompleteAccount()
                        }
                        is SignInResult.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.errorLoginText.text = signInState.message
                        }
                        else -> Unit
                    }
                }
        }
    }
    private fun userLogged(userId: String, groupId: String) {
        viewModel.createLoginSession(userId, groupId)
        navigateToHomeFragment()
    }

    private fun clearErrors(){
        binding.emailInput.error = null
        binding.passwordInput.error = null
    }

    private fun navigateToHomeFragment() {
        val request = NavDeepLinkRequest.Builder
            .fromUri("organized-app://com.odisby.organizedfw/home".toUri())
            .build()
        findNavController().navigate(request)
    }

    private fun navigateCompleteAccount() {
        val request = NavDeepLinkRequest.Builder
            .fromUri("organized-app://com.odisby.organizedfw/register_information".toUri())
            .build()
        findNavController().navigate(request)
    }

    private fun checkAll(): Boolean {
        val email = binding.emailInput.editText?.text.toString()
        val password = binding.passwordInput.editText?.text.toString()

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
        return true
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}