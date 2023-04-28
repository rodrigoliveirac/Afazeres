package com.rodcollab.afazeres.collections

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.rodcollab.afazeres.R
import com.rodcollab.afazeres.collections.adapters.CompletedTaskListAdapter
import com.rodcollab.afazeres.collections.adapters.UncompletedTaskListAdapter
import com.rodcollab.afazeres.databinding.FragmentTaskListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapterUncompletedTasks: UncompletedTaskListAdapter
    private lateinit var adapterCompletedTasks: CompletedTaskListAdapter

    private lateinit var viewModel: TaskListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TaskListViewModel::class.java]
        lifecycle.addObserver(TaskListObserver(viewModel))
        adapterUncompletedTasks = UncompletedTaskListAdapter(viewModel)
        adapterCompletedTasks = CompletedTaskListAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        createChannel(getString(R.string.channelId), getString(R.string.channelName))
        return binding.root
    }

    private fun createChannel(channelId:String,channelName:String) {
        val notificationChannel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
        }
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Task reminder"
        val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

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
        binding.currentDate.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                setupMaterialDatePicker()
            }
            true
        }
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
            viewModel.changeDate(it as Long)
        }
    }

    private fun bindUiState(uiState: TaskListViewModel.UiState) {


        totalTasks(uiState)

        binding.currentDate.text = uiState.date

        adapterUncompletedTasks.updateTasks(uiState.uncompletedTasks)
        adapterCompletedTasks.updateTasks(uiState.completedTasks)


    }

    private fun totalTasks(uiState: TaskListViewModel.UiState) {
        val totalUncompletedTasks = uiState.uncompletedTasks.size
        val totalCompleted = uiState.completedTasks.size

        if (totalUncompletedTasks == 0 && totalCompleted == 0)
            binding.total.text = getString(R.string.no_notes)
        else
            binding.total.text = getString(
                R.string.total_notes,
                totalUncompletedTasks.toString(),
                totalCompleted.toString()
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.taskRecyclerView.adapter = null
        binding.taskRecyclerViewCompleted.adapter = null
        _binding = null
    }
}
