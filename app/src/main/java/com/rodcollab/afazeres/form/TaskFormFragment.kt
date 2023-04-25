package com.rodcollab.afazeres.form

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.rodcollab.afazeres.R
import com.rodcollab.afazeres.databinding.FragmentTaskFormBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat


@AndroidEntryPoint
class TaskFormFragment : Fragment() {

    private var _binding: FragmentTaskFormBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: TaskFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TaskFormViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateField()

        binding.setAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->

            setupIcon(isChecked)

        }

        binding.saveButton.setOnClickListener { onSave() }
    }

    private fun setupIcon(isChecked: Boolean) {
        val icon = AppCompatResources.getDrawable(requireContext(), setIcon(isChecked))
        binding.setAlarmSwitch.setCompoundDrawablesRelativeWithIntrinsicBounds(
            icon,
            null,
            null,
            null
        )
    }

    private fun setIcon(isChecked: Boolean): Int {
        return when (isChecked) {
            true -> R.drawable.alarm_on
            else -> {
                R.drawable.alarm_off
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDateField() {

        binding.dateEditText.keyListener = null

        binding.dateEditText.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                setupMaterialDatePicker()
            }
            true
        }
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

        val habitCategory = when (binding.categoryChipGroup.checkedChipId) {
            R.id.category_study -> "Study"
            R.id.category_personal -> "Personal"
            else -> {
                "Work"
            }
        }


        val habitDate = binding.dateTextInput.editText?.text.toString()

        viewModel.addForm(habitName, habitCategory, habitDate)

        findNavController().navigateUp()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}