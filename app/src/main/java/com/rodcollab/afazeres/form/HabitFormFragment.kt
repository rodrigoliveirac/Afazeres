package com.rodcollab.afazeres.form

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.rodcollab.afazeres.collections.HabitListViewModel
import com.rodcollab.afazeres.databinding.FragmentHabitFormBinding
import com.rodcollab.afazeres.dummy.MockHabits
import java.text.SimpleDateFormat


class HabitFormFragment : Fragment() {

    private var _binding: FragmentHabitFormBinding? = null

    private val binding get() = _binding!!

    private val viewModel: HabitListViewModel by activityViewModels {
        HabitListViewModel.Factory(MockHabits)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateField()

        binding.saveButton.setOnClickListener { onSave() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDateField() {

        //to hide keyboard
        binding.dateEditText.keyListener = null

        //when the user click on the editText then show material date picker up
        binding.dateEditText.setOnTouchListener(OnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                setupMaterialDatePicker()
            }
            true
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupMaterialDatePicker() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy"))
        val picker = builder.build()
        picker.show(this.parentFragmentManager, "DATE_PICKER")

        picker.addOnPositiveButtonClickListener {
            binding.dateEditText.setText(picker.headerText)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun onSave() {

        val habitName = binding.titleTextInput.editText?.text.toString()

        val habitCategory = binding.categoryTextInput.editText?.text.toString()

        val habitDate = binding.dateTextInput.editText?.text.toString()

        viewModel.addHabit(habitName, habitCategory, habitDate)

        findNavController().navigateUp()

    }
}