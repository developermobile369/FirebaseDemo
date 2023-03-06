package com.android.firebasedemo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.firebasedemo.R
import com.android.firebasedemo.databinding.ActivityLoginBinding
import com.android.firebasedemo.model.User
import com.android.firebasedemo.viewmodel.MainViewModel
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private var authViewModel: MainViewModel? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var callbackManager: CallbackManager? = null
    private lateinit var auth: FirebaseAuth
    private val strSignUp = "Sign Up"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth

        initSignInButton()
        initMainViewModel()
        initGoogleSignInClient()
        initFacebook()
        initEmailPasswordAuth()
    }

    private fun initEmailPasswordAuth() {

        binding.btnSignUp.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length <= 6) {
                Toast.makeText(this, "Enter more than 6 digit password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.btnSignUp.text.toString() == strSignUp) {
                authViewModel?.signUpEmailPassword(email, password)
                authViewModel?.signUpLiveData?.observe(this) {
                    if (it) {
                        Toast.makeText(this, "Sign Up successfully", Toast.LENGTH_SHORT).show()
                        binding.btnSignUp.text = "Sign In"
                    }
                }
            } else {
                authViewModel?.signInEmailPassword(email, password)
                authViewModel?.signInpLiveData?.observe(this) {
                    if (it) {
                        Toast.makeText(this, "Sign In successfully", Toast.LENGTH_SHORT).show()
                        goToMainActivity()
                    }
                }
            }
        }

    }

    private fun initFacebook() {
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.setReadPermissions(listOf("email"))
        binding.loginButton.loginBehavior = LoginBehavior.WEB_ONLY

        binding.loginButton.registerCallback(callbackManager!!,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {

                }

                override fun onSuccess(result: LoginResult) {
                    authViewModel?.signInWithFacebook(this@LoginActivity, result)
                    authViewModel?.fbAuthenticatedUserLiveData?.observe(this@LoginActivity) {
                        if (it) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Facebook Login successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            goToMainActivity()
                        }
                    }
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val googleSignInAccount: GoogleSignInAccount =
                    task.getResult(ApiException::class.java)
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount)
                }
            } catch (e: ApiException) {
//                logErrorMessage(e.message)
            }
        }
    }

    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        authViewModel?.signInWithGoogle(googleAuthCredential)
        authViewModel?.authenticatedUserLiveData?.observe(this) { authenticatedUser ->
            Toast.makeText(
                this@LoginActivity,
                "Google Login successfully",
                Toast.LENGTH_SHORT
            )
                .show()
            goToMainActivity()
        }
    }

    private fun initSignInButton() {
        binding.btnSignIn.setOnClickListener { v: View? -> signIn() }
    }

    private fun initMainViewModel() {
        authViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, 111)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}