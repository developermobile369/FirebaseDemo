package com.android.firebasedemo.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.android.firebasedemo.model.User
import com.android.firebasedemo.repository.AuthRepository
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import kotlin.math.sign


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var authRepository: AuthRepository? = null
    var authenticatedUserLiveData: LiveData<User>? = null
    var fbAuthenticatedUserLiveData: LiveData<Boolean>? = null
    var signUpLiveData: LiveData<Boolean>? = null
    var signInpLiveData: LiveData<Boolean>? = null

    init {
        authRepository = AuthRepository()
    }


    fun signInWithGoogle(googleAuthCredential: AuthCredential?) {
        authenticatedUserLiveData = authRepository!!.signInGoogle(googleAuthCredential)
    }

    fun signInWithFacebook(context: AppCompatActivity, result: LoginResult) {
        fbAuthenticatedUserLiveData = authRepository!!.signInFacebook(context, result.accessToken)
    }

    fun signUpEmailPassword(email: String, password: String) {
        signUpLiveData = authRepository!!.signUpWithEmailPassword(email, password)
    }

    fun signInEmailPassword(email: String, password: String) {
        signInpLiveData = authRepository!!.signInWithEmailPassword(email, password)
    }

}