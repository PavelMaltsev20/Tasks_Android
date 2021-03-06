package com.pavelmaltsev.tasks.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pavelmaltsev.tasks.R
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

        binding.signUpBtnGroup.startFade()
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

        binding.includeAuth.layoutAuthBtn.setOnClickListener {
            checkUserInput()
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


    private fun checkUserInput() {
        val email = binding.includeAuth.layoutAuthEmail.text.toString()
        val pass = binding.includeAuth.layoutAuthPass.text.toString()
        val pass2 = binding.includeAuth.layoutAuthConfirmPass.text.toString()


        if (email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_fill_all_fields, Toast.LENGTH_LONG)
                .show()
            return
        }

        if (pass.length < 6 || pass2.length < 6) {
            Toast.makeText(
                requireContext(),
                R.string.short_password_please_choose_long_pass_min_6_symbols,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (pass != pass2) {
            Toast.makeText(
                requireContext(), R.string.please_enter_same_password, Toast.LENGTH_LONG
            ).show()
            return
        }

        super.signUpWithEmail(email, pass)

        displayEmailInput()
    }
}