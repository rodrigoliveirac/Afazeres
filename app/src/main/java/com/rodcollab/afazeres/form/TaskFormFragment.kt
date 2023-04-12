package com.rodcollab.afazeres.form

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.rodcollab.afazeres.core.database.AppDatabase
import com.rodcollab.afazeres.core.repository.TasksRepositoryImpl
import com.rodcollab.afazeres.databinding.FragmentTaskFormBinding
import java.text.SimpleDateFormat


class TaskFormFragment : Fragment() {

    private var _binding: FragmentTaskFormBinding? = null

    private val binding get() = _binding!!

    private val viewModel: TaskFormViewModel by activityViewModels {
        val db = AppDatabase.getInstance(requireContext())
        val tasksRepository = TasksRepositoryImpl(db)

        TaskFormViewModel.Factory(
            tasksRepository
        )
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

        binding.saveButton.setOnClickListener { onSave() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDateField() {

        binding.dateEditText.keyListener = null

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

        viewModel.addForm(habitName, habitCategory, habitDate)

        findNavController().navigateUp()

    }
}