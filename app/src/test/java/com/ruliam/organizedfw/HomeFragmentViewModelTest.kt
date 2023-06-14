package com.ruliam.organizedfw


class HomeFragmentViewModelTest {
    /**
     * InstantTaskExecutorRule swaps the background executor used by the Architecture Components
     * with a different one which executes each task synchronously.
     */
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//
//    private val testFinancesRepository = TestFinancesRepository()
//
//    private val viewModel = HomeFragmentViewModel(repository = testFinancesRepository)
//
//    @Before
//    fun setup() {
//        testFinancesRepository.financeList.clear()
//    }
//
//    @Test
//    fun `Verify if uiState is init with data`() {
//        // Prepare
//        testFinancesRepository.addNewRandomFinance()
//        // Execute
//        val uiState = viewModel.stateOnceAndStream().getOrAwaitValue()
//        // Verify
//        assert(uiState.financeItemList.isNotEmpty())
//    }
//
//    @Test
//    fun `Verify uiState is updated when new Habit is added`() {
//        // Prepare
//        testFinancesRepository.addNewRandomFinance()
//
//        // Execute
//        val uiStateInit = viewModel.stateOnceAndStream().getOrAwaitValue()
//        val initialHabitListSize = uiStateInit.financeItemList.size
//
//        viewModel.addRandomFinance() // Add new Habit
//
//        val updatedUiState = viewModel.stateOnceAndStream().getOrAwaitValue()
//        val currentSize = updatedUiState.financeItemList.size
//        val expectedSize = initialHabitListSize + 1 // Expected size be initial + 1
//
//        // Verify
//        assert(currentSize == expectedSize)
//    }
}