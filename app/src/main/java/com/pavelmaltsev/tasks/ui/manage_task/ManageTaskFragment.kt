package com.pavelmaltsev.tasks.ui.manage_task

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.databinding.FragmentManageTaskBinding
import com.pavelmaltsev.tasks.module.Task
import com.pavelmaltsev.tasks.ui.dialogs.calendar.CalendarDialog
import com.pavelmaltsev.tasks.ui.dialogs.calendar.OnDateSelected
import java.lang.String
import java.util.*

class ManageTaskFragment : Fragment(), OnDateSelected {

    private val TAG = "NewTaskFragment"
    private var _binding: FragmentManageTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(this).get(ManageViewModel::class.java)
    }
    private lateinit var selectedTask: Task

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSelectedTask()
        setDate()
        initListeners()
    }

    private fun checkSelectedTask() {
        try {
            val navArgs: ManageTaskFragmentArgs by navArgs()
            selectedTask = navArgs.selectedTask
            setTaskData()
        } catch (e: Exception) {
            Log.i(TAG, "task not selected")
        }
    }

    private fun setTaskData() {
        binding.manageTaskFragTitle.text = getText(R.string.update_task)
        binding.manageTaskRemove.visibility = View.VISIBLE
        binding.manageTaskTitle.setText(selectedTask.title)
        binding.manageTaskDesc.setText(selectedTask.desc)
        viewModel.calendar.timeInMillis = selectedTask.date
        viewModel.imageUri = selectedTask.imageUrl
        if (viewModel.imageUri.isNotEmpty())
            setImage()

        /**
         * it is possible that the user will be at zero point,
         * but the probability is very small
         *
         * */
        if (selectedTask.latitude != 0.0 && selectedTask.longitude != 0.0) {
            viewModel.setLocation(selectedTask.latitude, selectedTask.longitude)
            binding.manageTaskLocation.text = viewModel.getReadableLocation()
            binding.manageTaskOpenMap.visibility = View.VISIBLE
        }
    }

    //region Date selector
    override fun selectedDate(calendar: Calendar) {
        viewModel.calendar = calendar
        setDate()
    }

    private fun setDate() {
        binding.manageTaskDate.text =
            DateFormat.format("dd.MM.yyyy", viewModel.calendar)
    }
    //endregion

    private fun initListeners() {
        binding.manageTaskBtn.setOnClickListener {
            manageTask()
            hideKeyboard()
        }

        binding.manageTaskRemove.setOnClickListener {
            viewModel.removeTask(selectedTask)
            closeFragment()
        }

        binding.manageTaskDate.setOnClickListener {
            val calendar = CalendarDialog(this)
            activity?.let { activity ->
                calendar.show(activity.supportFragmentManager, "Calendar dialog")
            }
        }

        binding.manageTaskLocation.setOnClickListener {
            getUserLocation()
        }

        binding.manageTaskAddImage.setOnClickListener {
            openGallery()
        }

        binding.manageTaskOpenMap.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, viewModel.getMapUri())
            requireContext().startActivity(intent)
        }
    }

    private fun manageTask() {
        if (getTitle().isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_enter_title, Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(
            0,
            getDate(),
            getTitle(),
            getDesc(),
            false,
            viewModel.imageUri,
            viewModel.latitude,
            viewModel.longitude
        )

        if (this::selectedTask.isInitialized) {
            task.id = selectedTask.id
            viewModel.updateTask(task)
        } else {
            viewModel.addTask(task)
        }

        closeFragment()
    }

    //region Location
    private fun getUserLocation() {
        // Check if location permission granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            showPB()
            if (isGPSWork()) {
                getLocation()
            } else {
                hidePB()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_turn_on_gps),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isGPSWork(): Boolean {
        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            viewModel.LOCATION_REQUEST_CODE
        )
    }

    private fun getLocation() {
        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        LocationServices.getFusedLocationProviderClient(requireContext())
            .requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
    }

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            hidePB()

            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val lat = locationList.last().latitude
                val long = locationList.last().longitude
                viewModel.setLocation(lat, long)
                binding.manageTaskLocation.text = viewModel.getReadableLocation()
                binding.manageTaskOpenMap.visibility = View.VISIBLE
            }

            //We remove location updates because we need only one location
            LocationServices
                .getFusedLocationProviderClient(requireContext())
                .removeLocationUpdates(this)
        }

    }
    //endregion

    //region Image selector
    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.imageUri = result.data!!.data!!.toString()
                setImage()
            }
        }


    private fun setImage() {
        binding.manageTaskImage.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(viewModel.imageUri)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.manageTaskImage)
    }
    //endregion

    //region Getters
    private fun getDate() = viewModel.calendar.timeInMillis
    private fun getTitle() = binding.manageTaskTitle.text.toString()
    private fun getDesc() = binding.manageTaskDesc.text.toString()
    //endregion

    private fun hideKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun showPB() {
        binding.manageTaskPb.visibility = View.VISIBLE
        binding.manageTaskLocation.visibility = View.INVISIBLE
    }

    private fun hidePB() {
        binding.manageTaskPb.visibility = View.GONE
        binding.manageTaskLocation.visibility = View.VISIBLE
    }

    private fun closeFragment() {
        Navigation.findNavController(binding.root).popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}