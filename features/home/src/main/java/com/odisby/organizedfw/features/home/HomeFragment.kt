package com.odisby.organizedfw.features.home

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.odisby.organizedfw.core.data.util.UiStateFlow
import com.odisby.organizedfw.features.home.databinding.FragmentHomeBinding
import com.odisby.organizedfw.features.home.model.BalanceCardItem
import com.odisby.organizedfw.features.home.model.ListItemType
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

        financesAdapter = FinancesListAdapter(viewModel)
        balanceAdapter = BalanceListAdapter()
        navController = findNavController()
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
        binding.bottomInclude.financesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomInclude.financesRecyclerView.adapter = financesAdapter
        binding.balanceRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.balanceRecyclerView.adapter = balanceAdapter

        binding.transactionsButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Open expense form
        binding.expenseButton.setOnClickListener {
            navController.navigate(com.odisby.organizedfw.core.ui.R.id.action_homeFragment_to_expenseForm)
        }

        // Open income form
        binding.incomeButton.setOnClickListener {
            navController.navigate(com.odisby.organizedfw.core.ui.R.id.action_homeFragment_to_incomeForm)
        }

        binding.groupButton.setOnClickListener {
            navController.navigate(com.odisby.organizedfw.core.ui.R.id.action_homeFragment_to_groupPage)
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
    override fun onResume() {
        super.onResume()
        viewModel.getUiState()
//        binding.bottomAppBar.bottomNavigation.selectedItemId = R.id.homeFragment
    }

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
        val context: Context = ContextThemeWrapper(activity, com.odisby.organizedfw.core.ui.R.style.PopupMenu)
        val popup = PopupMenu(context, binding.topPanel.menuButton)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.topbar_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.menu_settings_option-> {
                    navController.navigate(com.odisby.organizedfw.core.ui.R.id.action_homeFragment_to_settingsFragment)
                }
            }
            true
        }
        popup.show()
    }

}