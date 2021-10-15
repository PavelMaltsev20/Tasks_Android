package com.pavelmaltsev.tasks.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.Navigation
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.auth.FirebaseAuth
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.databinding.FragmentLoginBinding

class LoginFragment : AuthFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Check if let user sign in
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            openTasksFragment()
        }

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

        startBlinkAnim()
        initListeners()
    }

    private fun startBlinkAnim() {
        val blink = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.blink
        )
        binding.signInNewAccount.startAnimation(blink)
    }

    private fun initListeners() {
        binding.signInEmail.setOnClickListener {
            displayEmailInput()
        }
        binding.includeAuth.layoutAuthBtn.setOnClickListener {
            checkUserInput()
        }
        binding.signInGoogle.setOnClickListener {
            super.authWithGoogle()
        }
        binding.signInFacebook.setReadPermissions("email", "public_profile")
        binding.signInFacebook.setOnClickListener {
            binding.signInFacebook.registerCallback(super.callbackManager, super.authWithFacebook())
        }

        binding.signInNewAccount.setOnClickListener {
            openRegisterFragment()
        }
    }

    private fun checkUserInput() {
        val email = binding.includeAuth.layoutAuthEmail.text.toString()
        val pass = binding.includeAuth.layoutAuthPass.text.toString()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_fill_all_fields, Toast.LENGTH_LONG)
                .show()
            return
        }

        if (pass.length < 6) {
            Toast.makeText(
                requireContext(),
                R.string.short_password_please_choose_long_pass_min_6_symbols,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        super.signInWithEmail(email, pass)

        displayEmailInput()
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
}