package com.pavelmaltsev.tasks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.FacebookSdk.sdkInitialize
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.pavelmaltsev.tasks.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val callbackManager = CallbackManager.Factory.create()
    private lateinit var binding: ActivityMainBinding
    private val TAG = "tester"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        LoginManager.getInstance().logOut();
//
//        sdkInitialize(FacebookSdk.getApplicationContext())
//        binding.loginButton.setReadPermissions("email", "public_profile")
//
//        binding.loginButton.registerCallback(
//            callbackManager,
//            object : FacebookCallback<LoginResult> {
//                override fun onCancel() {
//                    Toast.makeText(
//                        this@MainActivity,
//                        getString(R.string.action_canceled_please_check_facebook_login_and_password),
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//
//                override fun onError(error: FacebookException) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        getString(R.string.action_failed_please_check_internet_connection),
//                        Toast.LENGTH_LONG
//                    ).show()
//
//                }
//
//                override fun onSuccess(result: LoginResult) {
//                    firebaseAuthWithFacebook(result.accessToken)
//                }
//            });
    }

    /**
     *  super.onActivityResult - deprecated, but facebook still use this method
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun firebaseAuthWithFacebook(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val facebookUser = auth.currentUser!!
                    Toast.makeText(
                        this,
                        getString(R.string.welcome_to_tasks_app),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(TAG, "signInWithCredential error: ${task.exception}")
                }
            }
    }


}


