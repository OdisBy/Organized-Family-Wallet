package com.ruliam.organizedfw.features.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruliam.organizedfw.core.data.model.GroupUserDomain
import com.ruliam.organizedfw.features.group.databinding.FragmentPendingUsersBinding
import dagger.hilt.android.AndroidEntryPoint

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
        pendingUsersAdapter = UsersListAdapter()
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

        val pendingUsers = viewModel.uiState.value.data!!.pendingUsers
        bindPendingUsers(pendingUsers)
    }

    private fun bindPendingUsers(pendingUsers: List<GroupUserDomain?>) {
        pendingUsersAdapter.updateUsers(pendingUsers)
    }

    companion object {
        const val TAG = "PendingUsersPageFragment"
    }
}