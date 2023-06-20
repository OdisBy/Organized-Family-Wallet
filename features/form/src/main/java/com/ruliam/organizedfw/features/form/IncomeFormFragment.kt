package com.ruliam.organizedfw.features.form

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.ruliam.organizedfw.features.form.databinding.FragmentIncomeFormBinding
import com.ruliam.organizedfw.features.form.util.MyDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class IncomeFormFragment : Fragment() {

    private var _binding: FragmentIncomeFormBinding? = null

    private val binding get() = _binding!!

    private var selectedDate: Date? = null

    private lateinit var viewModel: FinancesFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FinancesFormViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeFormBinding.inflate(inflater, container, false)

        // Setting menu for dropdown textfield
        val typesOfExpense = resources.getStringArray(R.array.typesOfBudget)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, typesOfExpense)
        binding.typeOfIncomeTextView.setAdapter(arrayAdapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set data picker date
        binding.dateIncomeText.setOnClickListener {
            activity?.takeIf { !it.isFinishing && !it.isDestroyed }?.let { activity ->
                MyDatePickerDialog(activity, ::showDate).show()
            }
        }

        // Go to expenseFormFragment
        binding.expenseFormButton.setOnClickListener {
            findNavController().navigate(R.id.action_incomeFormFragment_to_expenseFormFragment)
        }

        // Go to homeFragment
        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        // Save Expense and Navigate Up
        binding.addNewIncomeButton.setOnClickListener { onSave() }
    }

    private fun onSave() {
        // Get values from input to save
        val expenseName = binding.nameOfBudgetField.editText?.text.toString()
        val typeOfExpense = binding.typeOfIncomeTextView.text.toString()
        val dateOfExpense = selectedDate!!
        val amount = binding.amountReceivedField.editText?.text.toString().toDouble()
        val isRecurrent = binding.recurrentCheck.isChecked
//        val isCouple = binding.coupleFinanceCheck.isChecked

        /**
         * call addFinance from viewModel
         * coroutine will try, if it run isSuccess will return true
         */
        viewModel.addFinance(
            expenseName,
            dateOfExpense,
            typeOfExpense,
            amount,
            isRecurrent
        ) { isSuccess ->
            if (isSuccess) {
                // Navigate Up to navigation three, goes back.
                findNavController().navigateUp()
            } else {
                // Handle failure
                Toast.makeText(requireContext(), "Failed to add finance.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun showDate(year: Int, month: Int, day: Int) {
        val selectedLocalDate = LocalDate.of(year, month + 1, day)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))
        val currentDateString = selectedLocalDate.format(formatter)

        // Select date to Date of java util
        selectedDate = Date.from(selectedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        binding.dateIncomeText.setText(currentDateString)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}