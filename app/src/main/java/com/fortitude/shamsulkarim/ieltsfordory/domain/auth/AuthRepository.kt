package com.fortitude.shamsulkarim.ieltsfordory.domain.auth

import android.app.Activity
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    interface AuthCallback {
        fun onSuccess(user: FirebaseUser)
        fun onFailure(e: Exception)
    }

    fun signIn(activity: Activity, callback: AuthCallback)
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
    fun isUserAuthenticated(): Boolean
}



