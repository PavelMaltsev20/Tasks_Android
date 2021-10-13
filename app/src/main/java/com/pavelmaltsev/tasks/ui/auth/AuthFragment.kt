package com.pavelmaltsev.tasks.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.FacebookSdk.sdkInitialize
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pavelmaltsev.tasks.R

open class AuthFragment : Fragment() {

    val callbackManager = CallbackManager.Factory.create()
    private val TAG = "AuthFragment"

    val fadeIn: Animation = AnimationUtils.loadAnimation(
        FacebookSdk.getApplicationContext(),
        R.anim.fade_in
    )
    val fadeOut: Animation = AnimationUtils.loadAnimation(
        FacebookSdk.getApplicationContext(),
        R.anim.fade_out
    )

    //region Facebook auth
    fun authWithFacebook(): FacebookCallback<LoginResult> {
        LoginManager.getInstance().logOut();
        sdkInitialize(FacebookSdk.getApplicationContext())

        return object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                showErrorToast()
            }

            override fun onError(error: FacebookException) {
                showErrorToast()
            }

            override fun onSuccess(result: LoginResult) {
                firebaseAuthWithFacebook(result.accessToken)
            }
        }
    }

    /**
     *  super.onActivityResult - deprecated, but facebook still use this method
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If we want use facebook data (Profile image, name, email and etc.)
                    // we can extract it from - facebookUser
                    val facebookUser = auth.currentUser!!
                    showWelcomeToast()
                    openTasksFragment()
                } else {
                    Log.e(TAG, "signInWithCredential error: ${task.exception}")
                }
            }
    }
    //endregion

    //region Google auth
    fun authWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getText(R.string.default_web_client_id) as String)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signInIntent = googleSignInClient.signInIntent
        onActivityResult.launch(signInIntent)
    }

    private val onActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    showErrorToast()
                    throw e
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //If we need additional data we can use googleUser,
                    //this is same like with facebook
                    val googleUser = auth.currentUser!!
                    showWelcomeToast()
                    openTasksFragment()
                } else {
                    showErrorToast()
                }
            }
    }
    //endregion

    //region Email auth
    fun signUpWithEmail(email: String, pass: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isComplete) {
                    if (task.isCanceled) {
                        showErrorToast()
                    }
                    if (task.isSuccessful) {
                        showWelcomeToast()
                        openTasksFragment()
                    }
                } else {
                    showErrorToast()
                }
            }
    }

    fun signInWithEmail(email: String, pass: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isComplete) {
                    if (task.isCanceled) {
                        showErrorToast()
                    }
                    if (task.isSuccessful) {
                        showWelcomeToast()
                        openTasksFragment()
                    }
                } else {
                    showErrorToast()
                }
            }
    }
    //endregion

    fun showWelcomeToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.welcome_to_tasks_app),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showErrorToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.error_occurred_please_check_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun openTasksFragment() {
        Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment))
            .navigate(R.id.action_global_tasksFragment)
    }

    fun Group.startFade() =
        referencedIds.map { rootView.findViewById<View>(it).startAnimation(fadeIn) }
}