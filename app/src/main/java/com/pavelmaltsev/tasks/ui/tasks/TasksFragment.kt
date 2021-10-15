package com.pavelmaltsev.tasks.ui.tasks

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.data.room.MainDatabase
import com.pavelmaltsev.tasks.databinding.FragmentTasksBinding
import com.pavelmaltsev.tasks.module.Task
import com.pavelmaltsev.tasks.ui.tasks.list.OnTaskListener
import com.pavelmaltsev.tasks.ui.tasks.list.TasksAdapter


class TasksFragment : Fragment(), OnTaskListener,
    NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val tasksAdapter = TasksAdapter(this)
    private val viewModel by lazy {
        ViewModelProvider(this).get(TaskViewModel::class.java)
    }
    private val userEmailIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.tasksList.adapter = tasksAdapter
        binding.tasksList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.tasksList.hasFixedSize()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDrawableLayout()

        binding.tasksFab.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_tasksFragment_to_manageTaskFragment)
        }

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

    override fun onTaskClick(task: Task) {
        val action = TasksFragmentDirections.actionTasksFragmentToManageTaskFragment(task)
        Navigation.findNavController(binding.root)
            .navigate(action)
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
        binding.tasksBotNav.setOnNavigationItemSelectedListener(this)
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

        binding.mainToolbar.navigationIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.LIGHTEN);

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
            R.id.list -> {
                Log.i("tester", "4: ")
            }
            R.id.map -> {
                Log.i("tester", "5: ")
            }
        }
        binding.tasksDrawable.closeDrawer(GravityCompat.START)
        return true
    }
    //endregion

}