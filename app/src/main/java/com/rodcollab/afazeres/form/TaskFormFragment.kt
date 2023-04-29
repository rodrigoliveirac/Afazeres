package com.rodcollab.afazeres.form

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.rodcollab.afazeres.R
import com.rodcollab.afazeres.databinding.FragmentTaskFormBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
@AndroidEntryPoint
class TaskFormFragment : Fragment() {

    private var _binding: FragmentTaskFormBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: TaskFormViewModel

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = requireContext().getSharedPreferences("TASK_REMINDER", 0)
        viewModel = ViewModelProvider(this)[TaskFormViewModel::class.java]
        lifecycle.addObserver(TaskFormObserver(_binding, viewModel))
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

        setupTimeField()

        setupAlarmSwitch()

        setupSaveButton()
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {

            viewModel.stateOnceAndStream().observe(viewLifecycleOwner) {
                if (binding.dateEditText.text.toString().isBlank()) {
                    mustSet("date")
                } else if (binding.timeEditText.text.toString().isBlank() && it.alarmActive) {
                    mustSet("time")
                } else {
                    onSave(it.alarmActive, it.reminderTime, it.datePicked)
                }
            }
        }
    }

    private fun setupAlarmSwitch() {


        binding.setAlarmSwitch.setOnCheckedChangeListener { btnView, isChecked ->
            setupIcon(isChecked)
            if (isChecked && viewModel.stateOnceAndStream().value?.alarmActive == false) {
                dialogSetReminderTime(btnView)
            } else {
                viewModel.alarmStatus(false)
            }
        }
    }

    private fun mustSet(objectMessage: String) {

        val alertDialog = MaterialAlertDialogBuilder(requireContext())

        setAttributes(objectMessage, alertDialog)

        onPositiveButton(objectMessage, alertDialog)

        val customAlertDialog = alertDialog.create()

        customAlertDialog.show()
    }

    private fun setAttributes(dateOrTime: String, alertDialog: MaterialAlertDialogBuilder) {
        alertDialog.setIcon(R.drawable.ic_warning)

        alertDialog.setTitle("You must set the $dateOrTime for the task")
    }

    private fun onPositiveButton(
        objectMessage: String,
        alertDialog: MaterialAlertDialogBuilder
    ) {
        alertDialog.setPositiveButton("Set the $objectMessage") { _, _ ->
            when (objectMessage) {
                "date" -> setupMaterialDatePicker()
                "time" -> setupMaterialTimePicker()
            }
        }
    }

    private fun formatTextTime(hour: Int, min: Int): String {
        return java.lang.String.format(
            Locale.getDefault(),
            "%02d:%02d",
            hour,
            min,
        )
    }

    private fun dialogSetReminderTime(
        btnView: CompoundButton
    ) {
        val alertDialog = MaterialAlertDialogBuilder(requireContext())

        // set the custom icon to the alert dialog
        alertDialog.setIcon(R.drawable.alarm_on)

        // title of the alert dialog
        alertDialog.setTitle("Send Reminder")

        val checkedItemDefault = 0

        val checkedItem = intArrayOf(checkedItemDefault)

        val listItems = arrayOf("1 hour before", "30 min before", "15 min before")

        alertDialog.setSingleChoiceItems(listItems, checkedItem[0]) { _, which ->
            checkedItem[0] = which
            viewModel.reminderTime(listItems[which])
        }


        alertDialog.setPositiveButton("Remind me") { dialog, _ ->
            viewModel.alarmStatus(true)
            dialog.dismiss()
        }

        alertDialog.setOnDismissListener {
            viewModel.stateOnceAndStream().observe(viewLifecycleOwner) {
                btnView.isChecked = it.alarmActive
                Log.d("AlarmActive", it.alarmActive.toString())
            }
        }

        alertDialog.setNegativeButton("Cancel") { _, _ ->
            viewModel.alarmStatus(false)
        }

        // create and build the AlertDialog instance with the AlertDialog builder instance
        val customAlertDialog = alertDialog.create()

        // show the alert dialog when the button is clicked
        customAlertDialog.show()
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

    private fun setupDateField() {

        binding.dateEditText.keyListener = null

        binding.dateEditText.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                setupMaterialDatePicker()
            }
            true
        }

    }

    private fun setupTimeField() {
        binding.timeEditText.keyListener = null
        binding.timeEditText.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                setupMaterialTimePicker()
            }
            true
        }
    }

    private fun setupMaterialTimePicker() {
        val builderTimePicker =
            MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H)
        val pickerTime =
            builderTimePicker.setTitleText("Choose the task's time.").build()

        pickerTime.show(this.parentFragmentManager, "fragment_tag")
        pickerTime.addOnPositiveButtonClickListener {
            val taskTime = formatTextTime(pickerTime.hour, pickerTime.minute)
            //TODO("test this later. you got he idea")
           //  viewModel.updateTimePicked(pickerTime.hour,pickerTime.minute)
            binding.timeEditText.setText(taskTime)
        }
    }

    private fun setupMaterialDatePicker() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy"))
        val picker = builder.build()
        picker.show(this.parentFragmentManager, "DATE_PICKER")

        picker.addOnPositiveButtonClickListener {

            viewModel.updateDatePicked(it as Long)
            binding.dateEditText.setText(picker.headerText)
        }



    }




    private fun onSave(isAlarmActive: Boolean, reminderTime: Long, taskDate: Long?) {

        val taskTitle = binding.titleTextInput.editText?.text.toString()
        val taskTime = binding.timeTextInput.editText?.text.toString()
        val taskCategory = getCategoryValue()

        viewModel.addForm(
            taskTitle,
            taskCategory,
            taskDate,
            taskTime,
            isAlarmActive,
            reminderTime
        )

        findNavController().navigateUp()
    }

    private fun getCategoryValue() = when (binding.categoryChipGroup.checkedChipId) {
        R.id.category_study -> "Study"
        R.id.category_personal -> "Personal"
        else -> {
            "Work"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}