package com.ruliam.organizedfw.features.group

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ruliam.organizedfw.core.ui.R
import com.ruliam.organizedfw.features.group.databinding.FragmentGroupPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class GroupPageFragment : Fragment() {

    private var _binding: FragmentGroupPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupPageViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()

        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry) { success ->
                if (!success) {
                    val startDestination = navController.currentDestination!!.id
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLogged()

        lifecycleScope.launch {
            viewModel.signInState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isLogged ->
                    if(isLogged){
                        getGroupId()
                        binding.groupInviteCode.text = viewModel.groupId
                    } else{
                        val request = NavDeepLinkRequest.Builder
                            .fromUri("organized-app://com.ruliam.organizedfw/signin".toUri())
                            .build()
                        findNavController().navigate(request)
                    }
                }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.groupInviteCodeLayout.setOnClickListener {
//            copyCodeToClipboard()
            shareInviteCode()
        }

    }

    private suspend fun getGroupId() {
        viewModel.getGroupId()
    }

    private fun shareInviteCode() {
        try{
            val inviteCode = binding.groupInviteCode.text
            val inviteMessage = "Venha organizar suas finanças comigo no app Organized com o link https://ruliams.live/join?code=$inviteCode. Ou entre com o código de convite: $inviteCode"

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, inviteMessage)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } catch (e: Exception){
            Log.w(TAG, "Error on share invite code: ${e.localizedMessage}")
        }
    }

    private fun copyCodeToClipboard() {
        try{
            val inviteCode = binding.groupInviteCode.text

            val clipboardManager = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", inviteCode)
            clipboardManager.setPrimaryClip(clipData)

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                Toast.makeText(context, "Text copied to clip board", Toast.LENGTH_SHORT)
                    .show()

            binding.iconGroupLayout.setImageResource(R.drawable.ic_check)

        } catch (e: Exception){
            binding.iconGroupLayout.setImageResource(R.drawable.ic_copy_content)
            Log.w(TAG, "Error on copy to clipboard: ${e.localizedMessage}")
        }

        binding.iconGroupLayout.setImageResource(R.drawable.ic_check)
    }

    companion object {
        const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
        const val TAG = "GroupPageFragment"
    }

}