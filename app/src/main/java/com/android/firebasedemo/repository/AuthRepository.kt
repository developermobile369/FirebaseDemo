package com.android.firebasedemo.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.android.firebasedemo.model.User
import com.facebook.AccessToken
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signInGoogle(googleAuthCredential: AuthCredential?): MutableLiveData<User> {
        val authenticatedUserMutableLiveData = MutableLiveData<User>()
        firebaseAuth.signInWithCredential(googleAuthCredential!!)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val user = User(uid, name, email)
                        authenticatedUserMutableLiveData.value = user
                    }
                } else {
//                logErrorMessage(authTask.getException().getMessage());
                }
            }
        return authenticatedUserMutableLiveData
    }

    fun signInFacebook(context: AppCompatActivity, token: AccessToken): MutableLiveData<Boolean> {
        val authenticatedUserMutableLiveData = MutableLiveData<Boolean>()

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(context) { task ->
                authenticatedUserMutableLiveData.value = task.isSuccessful
            }

        return authenticatedUserMutableLiveData
    }


    fun signUpWithEmailPassword(
        email: String,
        password: String
    ): MutableLiveData<Boolean> {
        val authenticatedUserMutableLiveData = MutableLiveData<Boolean>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    val auth = AuthUiState()
                    authenticatedUserMutableLiveData.value = task.isSuccessful
                }

            }
            .addOnFailureListener {
                it.printStackTrace()
            }

        return authenticatedUserMutableLiveData
    }

    fun signInWithEmailPassword(
        email: String,
        password: String
    ): MutableLiveData<Boolean> {
        val authenticatedUserMutableLiveData = MutableLiveData<Boolean>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                authenticatedUserMutableLiveData.value = task.isSuccessful
            }

        return authenticatedUserMutableLiveData
    }

    sealed class AuthUiState {
        object Loading : AuthUiState()
        data class Error(val message: String) : AuthUiState()
        data class Success(val item: Boolean?) : AuthUiState()
    }
}