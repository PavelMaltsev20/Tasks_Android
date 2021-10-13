package com.pavelmaltsev.tasks.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.facebook.FacebookSdk.getApplicationContext
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.databinding.FragmentLoginBinding

class LoginFragment : AuthFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInBtnGroup.startFade()

        initListeners()
    }

    private fun initListeners() {
        binding.signInEmail.setOnClickListener {
            displayEmailInput()
        }
        binding.signInGoogle.setOnClickListener {
        }
        binding.signInFacebook.setReadPermissions("email", "public_profile")
        binding.signInFacebook.setOnClickListener {
        }

        binding.signInTitle.setOnClickListener {
            openRegisterFragment()
        }
        binding.signInNewAccount.setOnClickListener {
            openRegisterFragment()
        }
    }

    private fun openRegisterFragment() {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun displayEmailInput() {
        binding.includeAuth.layoutAuthConfirmPass.visibility = View.GONE

        if (binding.includeAuth.layoutAuthParent.visibility == View.VISIBLE) {
            binding.includeAuth.layoutAuthParent.visibility = View.GONE
            binding.includeAuth.layoutAuthParent.startAnimation(super.fadeOut)
        } else {
            binding.includeAuth.layoutAuthParent.visibility = View.VISIBLE
            binding.includeAuth.layoutAuthParent.startAnimation(super.fadeIn)
        }
    }

    private fun Group.startFade() =
        referencedIds.map { rootView.findViewById<View>(it).startAnimation(super.fadeIn) }

}