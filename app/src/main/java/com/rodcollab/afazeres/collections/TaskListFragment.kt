package com.rodcollab.afazeres.collections

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.rodcollab.afazeres.R
import com.rodcollab.afazeres.collections.domain.GetCompletedTasksUseCaseImpl
import com.rodcollab.afazeres.collections.domain.GetUncompletedTasksUseCaseImpl
import com.rodcollab.afazeres.collections.domain.OnToggleTaskCompletedUseCaseImpl
import com.rodcollab.afazeres.core.repository.TasksRepositoryImpl
import com.rodcollab.afazeres.databinding.FragmentTaskListBinding
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.O)
class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: UncompletedTaskListAdapter
    private lateinit var adapterCompleted: CompletedTaskListAdapter
    private lateinit var observer: TaskListObserver

    private val viewModel: TaskListViewModel by activityViewModels {
        val tasksRepository = TasksRepositoryImpl()
        val onToggleTaskCompletedUseCase = OnToggleTaskCompletedUseCaseImpl(tasksRepository)
        val getCompletedTasksUseCase = GetCompletedTasksUseCaseImpl(tasksRepository)
        val getUncompletedTasksUseCase = GetUncompletedTasksUseCaseImpl(tasksRepository)

        TaskListViewModel.Factory(
            tasksRepository,
            onToggleTaskCompletedUseCase,
            getCompletedTasksUseCase,
            getUncompletedTasksUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = TaskListObserver(viewModel)
        lifecycle.addObserver(observer)
        adapter = UncompletedTaskListAdapter(viewModel)
        adapterCompleted = CompletedTaskListAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerView.adapter = adapter

        binding.taskRecyclerViewCompleted.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerViewCompleted.adapter = adapterCompleted

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
            findNavController().navigate(R.id.taskFormFragment)
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

    private fun bindUiState(uiState: TaskListViewModel.UiState) {

        val totalIncomplete = uiState.uncompletedTasks.size
        val totalCompleted = uiState.completedTasks.size

        if (totalIncomplete == 0 && totalCompleted == 0)
            binding.total.text = "You don't have tasks"
        else
            binding.total.text =
                "$totalIncomplete incomplete, $totalCompleted completed"


        adapter.updateTasks(uiState.uncompletedTasks)
        adapterCompleted.updateTasks(uiState.completedTasks)

        binding.currentDate.text = uiState.date
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
