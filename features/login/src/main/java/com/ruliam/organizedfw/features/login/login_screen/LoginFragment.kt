package com.ruliam.organizedfw.features.login.login_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ruliam.organizedfw.core.data.util.SignInResult
import com.ruliam.organizedfw.core.ui.R
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

    private lateinit var savedStateHandle: SavedStateHandle


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

        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

        binding.progressBar.visibility = View.INVISIBLE

        if(viewModel.startLogged){
            navigateToHomeFragment()
        }

        binding.signUpButton.setOnClickListener {
//            val request = NavDeepLinkRequest.Builder
//                .fromUri("organized-app://com.ruliam.organizedfw/signup".toUri())
//                .build()
//            val request = NavDeepLinkBuilder(requireContext())
//                .setDestination()
            findNavController().navigate(R.id.action_login_to_sign_up)
//            findNavController().navigate(com.ruliam.organizedfw.core.ui.R.id.action_login_to_sign_up)
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
                            signSuccess(signInState)
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

    private fun signSuccess(signInState: SignInResult.Success) {
        binding.progressBar.visibility = View.INVISIBLE
        userLogged(signInState.userId, signInState.groupId)
        savedStateHandle.set(LOGIN_SUCCESSFUL, true)
        findNavController().popBackStack()
    }

    private fun userLogged(userId: String, groupId: String) {
        viewModel.createLoginSession(userId, groupId)
//        navigateToHomeFragment()
    }

    private fun clearErrors(){
        binding.emailInput.error = null
        binding.passwordInput.error = null
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_login_to_home)
    }

    private fun navigateCompleteAccount() {
        val request = NavDeepLinkRequest.Builder
            .fromUri("organized-app://com.ruliam.organizedfw/register_information".toUri())
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
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
    }
}