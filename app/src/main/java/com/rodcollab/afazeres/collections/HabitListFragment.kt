package com.rodcollab.afazeres.collections

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.rodcollab.afazeres.R
import com.rodcollab.afazeres.databinding.FragmentHabitListBinding
import com.rodcollab.afazeres.dummy.MockHabits

@RequiresApi(Build.VERSION_CODES.O)
class HabitListFragment : Fragment() {

    private var _binding: FragmentHabitListBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: HabitListAdapter
    private lateinit var adapterCompleted: HabitListCompletedAdapter

    private val viewModel: HabitListViewModel by activityViewModels {
        HabitListViewModel.Factory(MockHabits)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = HabitListAdapter(viewModel)
        adapterCompleted = HabitListCompletedAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitListBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.habitRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.habitRecyclerView.adapter = adapter

        binding.habitRecyclerViewCompleted.layoutManager = LinearLayoutManager(requireContext())
        binding.habitRecyclerViewCompleted.adapter = adapterCompleted

        viewModel
            .stateOnceAndStream()
            .observe(viewLifecycleOwner) {
                bindUiState(it)
            }

        binding.currentDate.setOnTouchListener(View.OnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                setupMaterialDatePicker()
            }
            true
        })

        binding.fab.setOnClickListener {
          findNavController().navigate(R.id.habitFormFragment)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupMaterialDatePicker() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        //.setTextInputFormat(SimpleDateFormat("dd/MM/yyyy"))
        val picker = builder.build()
        picker.show(this.parentFragmentManager, "DATE_PICKER")

        picker.addOnPositiveButtonClickListener {
            viewModel.changeDate(picker.headerText)
        }
    }

    private fun bindUiState(uiState: HabitListViewModel.UiState) {

        val totalIncomplete = uiState.uncompletedTasks.size
        val totalCompleted = uiState.completedTasks.size

        if (totalIncomplete == 0 && totalCompleted == 0)
            binding.total.text = "You don't have tasks"
        else
            binding.total.text =
                "$totalIncomplete incomplete, $totalCompleted completed"


        adapter.updateHabits(uiState.uncompletedTasks)
        adapterCompleted.updateHabits(uiState.completedTasks)

        binding.currentDate.text = uiState.date
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
