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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.rodcollab.afazeres.R
import com.rodcollab.afazeres.collections.domain.DeleteTaskUseCaseImpl
import com.rodcollab.afazeres.collections.domain.GetCompletedTasksUseCaseImpl
import com.rodcollab.afazeres.collections.domain.GetUncompletedTasksUseCaseImpl
import com.rodcollab.afazeres.collections.domain.OnToggleTaskCompletedUseCaseImpl
import com.rodcollab.afazeres.core.database.AppDatabase
import com.rodcollab.afazeres.core.repository.TasksRepositoryImpl
import com.rodcollab.afazeres.databinding.FragmentTaskListBinding
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.O)
class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null

    private val binding get() = _binding!!

    private lateinit var scope: CoroutineScope
    private lateinit var adapterUncompletedTasks: UncompletedTaskListAdapter
    private lateinit var adapterCompletedTasks: CompletedTaskListAdapter
    private lateinit var observer: TaskListObserver

    private val viewModel: TaskListViewModel by activityViewModels {
        val db = AppDatabase.getInstance(requireContext())
        val tasksRepository = TasksRepositoryImpl(db)
        val deleteTaskUseCase = DeleteTaskUseCaseImpl(tasksRepository)
        val onToggleTaskCompletedUseCase = OnToggleTaskCompletedUseCaseImpl(tasksRepository)
        val getCompletedTasksUseCase = GetCompletedTasksUseCaseImpl(tasksRepository)
        val getUncompletedTasksUseCase = GetUncompletedTasksUseCaseImpl(tasksRepository)

        TaskListViewModel.Factory(
            deleteTaskUseCase,
            onToggleTaskCompletedUseCase,
            getCompletedTasksUseCase,
            getUncompletedTasksUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope = CoroutineScope(Dispatchers.Main + Job())
        observer = TaskListObserver(viewModel)
        lifecycle.addObserver(observer)
        adapterUncompletedTasks = UncompletedTaskListAdapter(viewModel)
        adapterCompletedTasks = CompletedTaskListAdapter(viewModel)
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

        setupUncompletedTasksAdapter()

        setupCompletedTasksAdapter()

        observeTasks()

        setupPickerCurrentDate()

        addTaskFab()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPickerCurrentDate() {
        binding.currentDate.setOnTouchListener(View.OnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                setupMaterialDatePicker()
            }
            true
        })
    }

    private fun addTaskFab() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.taskFormFragment)
        }
    }

    private fun observeTasks() {
        viewModel
            .stateOnceAndStream()
            .observe(viewLifecycleOwner) {
                bindUiState(it)
            }
    }

    private fun setupCompletedTasksAdapter() {
        binding.taskRecyclerViewCompleted.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerViewCompleted.adapter = adapterCompletedTasks
    }

    private fun setupUncompletedTasksAdapter() {
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerView.adapter = adapterUncompletedTasks

        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapterUncompletedTasks))
        itemTouchHelper.attachToRecyclerView(binding.taskRecyclerView)
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

        scope.launch {
            totalTasks(uiState)
        }

        adapterUncompletedTasks.updateTasks(uiState.uncompletedTasks)
        adapterCompletedTasks.updateTasks(uiState.completedTasks)

        binding.currentDate.text = uiState.date
    }

    private fun totalTasks(uiState: TaskListViewModel.UiState) {
        val totalIncomplete = uiState.completedTasks.size
        val totalCompleted = uiState.uncompletedTasks.size

        if (totalIncomplete == 0 && totalCompleted == 0)
            binding.total.text = "You don't have tasks"
        else
            binding.total.text =
                "$totalIncomplete incomplete, $totalCompleted completed"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
        _binding = null
    }
}
