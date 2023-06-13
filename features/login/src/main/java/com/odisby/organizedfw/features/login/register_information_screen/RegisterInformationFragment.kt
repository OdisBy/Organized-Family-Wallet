package com.odisby.organizedfw.features.login.register_information_screen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.odisby.organizedfw.core.data.util.AuthResult
import com.odisby.organizedfw.core.data.util.UiStateFlow
import com.odisby.organizedfw.features.login.R
import com.odisby.organizedfw.features.login.databinding.FragmentRegisterInformationBinding
import com.odisby.organizedfw.features.login.signup_screen.SignUpFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class RegisterInformationFragment : Fragment() {

    private var _binding: FragmentRegisterInformationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RegisterInformationViewModel

    private lateinit var navController: NavController

    private lateinit var setAvatar: ActivityResultLauncher<Intent>

    private var buttonStep: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[RegisterInformationViewModel::class.java]
        navController = findNavController()
        viewModel.getUiState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterInformationBinding.inflate(inflater, container, false)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAvatar = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedUri: Uri? = data?.data
                selectedUri?.let { uri ->
                    val imageBitmap = getBitmapFromUri(uri)
                    imageBitmap?.let {
                        try{
                            viewModel.updateAvatarView(imageBitmap)
                        } catch (e: Exception){
                            Log.w(TAG, "Error on updateAvatar ${e.message.toString()}")
                        }

                    }
                }
            }
        }
        binding.progressBar.visibility = View.INVISIBLE

        binding.button.setOnClickListener {
            if(checkAll()){
                confirmButton()
            }
        }

        binding.cameraButton.setOnClickListener {
            chooseAvatar()
        }

        lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when(uiState){
                        is UiStateFlow.Error -> {
                            Snackbar.make(
                                binding.root,
                                uiState.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is UiStateFlow.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is UiStateFlow.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            bindUiState(uiState.data!!)
                        }
                        else -> Unit
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.updateState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when(uiState){
                        is AuthResult.Error -> {
                            Snackbar.make(
                                binding.root,
                                uiState.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is AuthResult.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is AuthResult.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            navigateToHomeFragment()
                        }
                        else -> Unit
                    }
                }
        }
    }


    private fun chooseAvatar() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        setAvatar.launch(intent)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun checkAll(): Boolean {
        return if(buttonStep == 0){
            val username = binding.usernameInput.editText?.text.toString()
            checkUsername(username)
            viewModel.generateAvatar(username)
            false
        } else{
            val username = binding.usernameInput.editText?.text.toString()
            val groupId = binding.groupInput.editText?.text.toString()
            checkUsername(username) || checkGroupInvite(groupId)
        }
    }

    private fun checkUsername(username: String): Boolean{
        if(username.isEmpty()){
            binding.usernameInput.error = REQUIRED_FIELD
            return false
        } else if(username.length < 2){
            binding.usernameInput.error = "Nome deve ter pelo menos 2 caracteres"
            return false
        }
        binding.usernameInput.error = null


        binding.groupInput.visibility = View.VISIBLE
        binding.groupTooltip.visibility = View.VISIBLE

        binding.avatar.visibility = View.VISIBLE
        binding.cameraButton.visibility = View.VISIBLE

        binding.button.text = "Concluir"
        binding.button.setIconResource(com.odisby.organizedfw.core.ui.R.drawable.ic_check)

        buttonStep++
        return true
    }

    private fun checkGroupInvite(groupId: String): Boolean{
        if(groupId.isEmpty()){
            binding.groupInput.error = null
            return true
        }
        else if(groupId.length != 6){
            binding.groupInput.error = "Id do grupo deve ser vázio ou ter 6 caracteres"
            return false
        }
        binding.groupInput.error = null
        return true
    }


    private fun confirmButton() {
        val avatar = binding.avatar.drawToBitmap()
        val groupId = binding.groupInput.editText?.text?.toString()
        val username = binding.usernameInput.editText?.text.toString()
        val uiData = RegisterInformationViewModel.UiState(username, avatar, groupId)
        viewModel.createUser(uiData)
    }

    private fun bindUiState(uiState: RegisterInformationViewModel.UiState){
        if(uiState.profileAvatar != null){
            binding.avatar.setImageBitmap(uiState.profileAvatar)
        }
        if(uiState.groupInvite != null){
            binding.groupTextInput.setText(uiState.groupInvite)
        }
        if(uiState.username != null){
            binding.usernameTextInput.setText(uiState.username)
        }
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(com.odisby.organizedfw.core.ui.R.id.action_registerInformationFragment_to_app_graph)
    }

    companion object {
        private const val TAG = "RegisterInfoFragment"
        private const val REQUIRED_FIELD = "Esse campo é obrigatório"
    }
}