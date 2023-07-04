package com.ruliam.organizedfw.features.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ruliam.organizedfw.core.data.model.GroupUserDomain
import com.ruliam.organizedfw.core.data.util.UiStateFlow
import com.ruliam.organizedfw.features.group.databinding.FragmentPendingUsersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingUsersFragment : Fragment() {
    private var _binding: FragmentPendingUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupPageViewModel by viewModels()

    private lateinit var navController: NavController

    private lateinit var pendingUsersAdapter: UsersListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        pendingUsersAdapter = UsersListAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pendingUsersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pendingUsersRecyclerView.adapter = pendingUsersAdapter
        viewModel.getUiState()

        binding.backButton.setOnClickListener {
            navController.popBackStack()
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
                            bindPendingUsers(uiState.data!!.pendingUsers)
                        }
                        else -> Unit
                    }
                }
        }


        lifecycleScope.launch {
            viewModel.dialogState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { dialog ->
                    when(dialog){
                        is GroupPageViewModel.DialogModel.BindUser -> {
                            openBindUser(dialog.user)
                            viewModel.emptyDialog()
                        }

                        else -> Unit
                    }
                }
        }
    }
    private fun bindPendingUsers(pendingUsers: List<GroupUserDomain?>) {
        pendingUsersAdapter.updateUsers(pendingUsers)
    }


    private fun openBindUser(user: GroupUserDomain) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${user.username} deseja se juntar a vocês!")
            .setMessage("Você permite que ${user.username} entre em seu grupo?")
            .setPositiveButton("Permitir") { dialog, which ->
                viewModel.addPendingUser(user)
                val pendingUsers = viewModel.uiState.value.data!!.pendingUsers
                bindPendingUsers(pendingUsers)
            }
            .setNegativeButton("Negar") { dialog, which ->
                viewModel.deniedPendingUser(user)
            }
            .setNeutralButton("Depois") { dialog, which ->

            }
            .show()
    }

    companion object {
        const val TAG = "PendingUsersPageFragment"
    }
}