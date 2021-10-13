package com.pavelmaltsev.tasks.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pavelmaltsev.tasks.databinding.FragmentRegisterBinding


class RegisterFragment : AuthFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.signUpGoogle.setOnClickListener {
            super.authWithGoogle()
        }

        //Permissions for extract data from Facebook profile
        binding.signUpFacebook.setReadPermissions("email", "public_profile")
        binding.signUpFacebook.setOnClickListener {
            binding.signUpFacebook.registerCallback(super.callbackManager, super.authWithFacebook())
        }

        binding.signUpEmail.setOnClickListener {
            displayEmailInput()
        }
    }

    private fun displayEmailInput() {
        binding.includeAuth.layoutAuthConfirmPass.visibility = View.VISIBLE

        if (binding.includeAuth.layoutAuthParent.visibility == View.VISIBLE) {
            binding.includeAuth.layoutAuthParent.visibility = View.GONE
            binding.includeAuth.layoutAuthParent.startAnimation(super.fadeOut)
        } else {
            binding.includeAuth.layoutAuthParent.visibility = View.VISIBLE
            binding.includeAuth.layoutAuthParent.startAnimation(super.fadeIn)
        }
    }
}