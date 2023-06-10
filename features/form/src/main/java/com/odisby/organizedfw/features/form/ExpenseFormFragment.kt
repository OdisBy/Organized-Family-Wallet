package com.odisby.organizedfw.features.form

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.odisby.organizedfw.features.form.databinding.FragmentExpenseFormBinding
import com.odisby.organizedfw.features.form.util.MyDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class ExpenseFormFragment : Fragment() {

    private var _binding: FragmentExpenseFormBinding? = null

    private val binding get() = _binding!!

    private var selectedDate: Date? = null

    private lateinit var viewModel: FinancesFormViewModel

    private var zoneId = ZoneId.of("America/Sao_Paulo")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[FinancesFormViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseFormBinding.inflate(inflater, container, false)

        // Setting menu for dropdown textfield
        val typesOfExpense = resources.getStringArray(R.array.typesOfExpenses)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, typesOfExpense)
        binding.typeOfExpenseTextView.setAdapter(arrayAdapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set data picker date
        binding.dateExpenseText.setOnClickListener {
            activity?.takeIf { !it.isFinishing && !it.isDestroyed }?.let { activity ->
                MyDatePickerDialog(activity, ::showDate).show()
            }
        }

        // Go to incomeFragment
        binding.incomeFormButton.setOnClickListener { findNavController().navigate(
            com.odisby.organizedfw.core.ui.R.id.action_expenseFormFragment_to_incomeFormFragment) }

        // Back to homeFragment
        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        // Save Expense and Navigate Up
        binding.addNewExpenseButton.setOnClickListener { onSave() }
    }

    private fun onSave(){
        // Get values from input to save
        val expenseName = binding.nameOfExpenseField.editText?.text.toString()
        val typeOfExpense = binding.typeOfExpenseTextView.text.toString()
        val dateOfExpense = selectedDate!!
        val amount = binding.amountSpentField.editText?.text.toString().toDouble()
        val isRecurrent = binding.recurrentCheck.isChecked
//        val isCouple = binding.coupleFinanceCheck.isChecked

        val expenseAmount = amount * -1

        viewModel.addFinance(
            expenseName,
            dateOfExpense,
            typeOfExpense,
            expenseAmount,
            isRecurrent
        ) {isSuccess ->
            if (isSuccess) {
                // Navigate Up to navigation three, goes back.
                findNavController().navigateUp()
            } else {
                // Handle failure
                Toast.makeText(requireContext(), "Failed to add finance.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDate(year: Int, month: Int, day: Int) {
        val selectedLocalDate = LocalDate.of(year, month + 1, day)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))
        val currentDateString = selectedLocalDate.format(formatter)

        val currentDate = LocalDate.now()
        val selectedDateTime = if (selectedLocalDate == currentDate) {
            val currentDateTime = LocalDateTime.now(zoneId)
            currentDateTime
        } else {
            val endOfDay = LocalTime.MAX
            val zonedDateTime = ZonedDateTime.of(selectedLocalDate, endOfDay, zoneId)
            val selectedDateTime = zonedDateTime.toLocalDateTime()
            selectedDateTime
        }

        selectedDate = Date.from(selectedDateTime.atZone(zoneId).toInstant())

        binding.dateExpenseText.setText(currentDateString)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}