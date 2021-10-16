package com.pavelmaltsev.tasks.ui.tasks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.data.enum.DisplayMode
import com.pavelmaltsev.tasks.data.room.MainDatabase
import com.pavelmaltsev.tasks.databinding.FragmentTasksBinding
import com.pavelmaltsev.tasks.module.Task
import com.pavelmaltsev.tasks.ui.tasks.list.listeners.OnUpdateListener
import com.pavelmaltsev.tasks.ui.tasks.list.TasksAdapter
import com.pavelmaltsev.tasks.ui.tasks.list.listeners.OnCompleteListener

class TasksFragment :
    Fragment(),
    OnUpdateListener,
    OnCompleteListener,
    NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val tasksAdapter = TasksAdapter(this, this)
    private val sharedPref by lazy {
        requireContext().getSharedPreferences(getString(R.string.shared_pref_name), 0)
    }
    private val editor by lazy { sharedPref.edit() }
    private val viewModel by lazy {
        ViewModelProvider(this).get(TaskViewModel::class.java)
    }
    private val userEmailIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initBottomNavView()
        initDrawableLayout()
        initListener()
        initObserver()
    }

    private fun initRecyclerView() {
        if (sharedPref.getBoolean(getString(R.string.is_list_mode), true)) {
            binding.tasksBotNav.selectedItemId = R.id.list
            displayListMode()
        } else {
            binding.tasksBotNav.selectedItemId = R.id.map
            displayMapMode()
        }

        binding.tasksList.adapter = tasksAdapter
        binding.tasksList.hasFixedSize()
    }

    private fun displayListMode() {
        editor.putBoolean(getString(R.string.is_list_mode), true).apply()
        binding.tasksList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        tasksAdapter.setListMode(DisplayMode.LIST_MODE)
    }

    private fun displayMapMode() {
        editor.putBoolean(getString(R.string.is_list_mode), false).apply()
        binding.tasksList.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        tasksAdapter.setListMode(DisplayMode.MAP_MODE)
    }


    private fun initListener() {
        binding.tasksFab.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_tasksFragment_to_manageTaskFragment)
        }
    }

    private fun initObserver() {
        viewModel.tasksList.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.tasksEmptyList.visibility = View.VISIBLE
            } else {
                binding.tasksEmptyList.visibility = View.GONE
            }
            tasksAdapter.setList(it)
            tasksAdapter.notifyDataSetChanged()
        })
    }

    override fun onUpdate(task: Task) {
        val action = TasksFragmentDirections.actionTasksFragmentToManageTaskFragment(task)
        Navigation.findNavController(binding.root)
            .navigate(action)
    }

    private fun initBottomNavView() {
        binding.tasksBotNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.list -> {
                    displayListMode()
                }
                R.id.map -> {
                    displayMapMode()
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    //region Drawable
    private fun initDrawableLayout() {
        setUpNavigationView()
        setUserEmailInDrawable()
        setToolbarDate()
        setToggleActionBar()
    }

    private fun setUserEmailInDrawable() {
        val menu = binding.mainNavView.menu
        val menuItem = menu.getItem(userEmailIndex)
        menuItem.title = FirebaseAuth.getInstance().currentUser!!.email
    }

    private fun setUpNavigationView() {
        binding.mainNavView.setNavigationItemSelectedListener(this)
        binding.mainNavView.bringToFront()
    }

    private fun setToolbarDate() {
        binding.tasksToolbarDate.text = DateFormat.format(
            "dd.MM.yyyy",
            System.currentTimeMillis()
        )
    }

    private fun setToggleActionBar() {
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.tasksDrawable,
            binding.mainToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.tasksDrawable.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.check_on_github -> {
                val uri =
                    Uri.parse("https://github.com/PavelMaltsev20/Tasks_Android")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                MainDatabase.INSTANCE = null
                requireActivity().finish()
            }
            R.id.explanation -> {
                editor.putBoolean(getString(R.string.is_explanation_showed), false).apply()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.close_and_open_the_app_to_see_the_explanation),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.tasksDrawable.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onComplete(task: Task) {
        task.isComplete = !task.isComplete
        viewModel.updateTask(task)
    }
    //endregion

}