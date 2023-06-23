package com.ruliam.organizedfw.features.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.ruliam.organizedfw.core.data.util.UiStateFlow
import com.ruliam.organizedfw.features.home.databinding.FragmentHomeBinding
import com.ruliam.organizedfw.features.home.model.BalanceCardItem
import com.ruliam.organizedfw.features.home.model.ListItemType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private lateinit var financesAdapter: FinancesListAdapter

    private lateinit var balanceAdapter: BalanceListAdapter

    private lateinit var viewModel: HomeFragmentViewModel

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeFragmentViewModel::class.java]

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

        financesAdapter = FinancesListAdapter(viewModel)
        balanceAdapter = BalanceListAdapter()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomInclude.bottomSheet)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLogged()
        lifecycleScope.launch {
            viewModel.signInState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isLogged ->
                    if(!isLogged){
                        val request = NavDeepLinkRequest.Builder
                            .fromUri("organized-app://com.ruliam.organizedfw/signin".toUri())
                            .build()
                        navController.navigate(request)
                    } else {
                        viewModel.getUiState()
                    }
                }
        }

        binding.bottomInclude.financesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomInclude.financesRecyclerView.adapter = financesAdapter
        binding.balanceRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.balanceRecyclerView.adapter = balanceAdapter

        binding.transactionsButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Open expense form
        binding.expenseButton.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("organized-app://com.ruliam.organizedfw.form/expense".toUri())
                .build()
            navController.navigate(request)
//            navController.navigate(com.ruliam.organizedfw.core.ui.R.id.action_homeFragment_to_expenseForm)
        }

        // Open income form
        binding.incomeButton.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("organized-app://com.ruliam.organizedfw.form/income".toUri())
                .build()
            navController.navigate(request)
//            navController.navigate(com.ruliam.organizedfw.core.ui.R.id.action_homeFragment_to_incomeForm)
        }

        binding.groupButton.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("organized-app://com.ruliam.organizedfw.group/page".toUri())
                .build()
            navController.navigate(request)
//            navController.navigate(com.ruliam.organizedfw.core.ui.R.id.action_homeFragment_to_groupPageFragment)
        }

        binding.buttonCardview6.setOnClickListener {
            val uuid = "123456"
            val request = NavDeepLinkRequest.Builder
                .fromUri("organized-app://com.ruliam.organizedfw.group/enter/$uuid".toUri())
                .build()
            navController.navigate(request)
        }

        binding.topPanel.menuButton.setOnClickListener {
            showTopPopup()
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
                            binding.flowItems.visibility = View.INVISIBLE
                            binding.balanceRecyclerView.visibility = View.INVISIBLE
                        }
                        is UiStateFlow.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.flowItems.visibility = View.VISIBLE
                            bindUiState(uiState.data!!)
                            bindCards(uiState.data!!.balanceCardList)
                            bindFinances(uiState.data!!.financeEntityList)
                        }
                        else -> Unit
                    }
                }
        }
    }
//    override fun onResume() {
//        super.onResume()
//        viewModel.getUiState()
////        binding.bottomAppBar.bottomNavigation.selectedItemId = R.id.homeFragment
//    }

    private fun bindFinances(finances: List<ListItemType>) {
        financesAdapter.updateFinances(finances)
    }

    private fun bindCards(cards: List<BalanceCardItem>) {
        balanceAdapter.updateCard(cards)
        binding.balanceRecyclerView.visibility = View.VISIBLE
    }

    private fun bindUiState(uiState: HomeFragmentViewModel.UiState) {
        binding.topPanel.profilePhoto.setImageBitmap(uiState.avatar)
        binding.topPanel.welcomeName.text = uiState.welcomeName
        binding.topPanel.welcomeDay.text = uiState.welcomeDay
    }


    private fun showTopPopup(){
        val context: Context = ContextThemeWrapper(activity, com.ruliam.organizedfw.core.ui.R.style.PopupMenu)
        val popup = PopupMenu(context, binding.topPanel.menuButton)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.topbar_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.menu_settings_option-> {
//                    navController.navigate(com.ruliam.organizedfw.core.ui.R.id.action_homeFragment_to_settingsFragment)
                    val request = NavDeepLinkRequest.Builder
                        .fromUri("organized-app://com.ruliam.organizedfw/settings".toUri())
                        .build()
                    navController.navigate(request)
                }
            }
            true
        }
        popup.show()
    }

    companion object {
        const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
        const val TAG = "HomeFragment"
    }

}