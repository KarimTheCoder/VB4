package com.fortitude.shamsulkarim.ieltsfordory.data.auth

import android.app.Activity
import android.content.Context
import android.os.CancellationSignal
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseAuthRepository(private val context: Context) : AuthRepository {
    companion object { private const val TAG = "AuthRepository" }

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    override fun signIn(activity: Activity, callback: AuthRepository.AuthCallback) {
        Log.d(TAG, "signIn: initiated")
        val clientId = context.getString(R.string.google_client_id)
        Log.d(TAG, "signIn: using Client ID: $clientId")

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        Log.d(TAG, "signIn: requesting credential")
        credentialManager.getCredentialAsync(
            activity,
            request,
            CancellationSignal(),
            ContextCompat.getMainExecutor(activity),
            object : CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                override fun onResult(result: GetCredentialResponse) {
                    Log.d(TAG, "onResult: credential received")
                    val credential: Credential = result.credential
                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            firebaseAuthWithGoogle(googleIdTokenCredential.idToken, callback)
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.e(TAG, "Failed to parse Google ID token", e)
                            callback.onFailure(e)
                        }
                    } else {
                        val msg = "Unexpected type of credential: ${credential.type}"
                        Log.e(TAG, msg)
                        callback.onFailure(Exception(msg))
                    }
                }

                override fun onError(e: GetCredentialException) {
                    Log.e(TAG, "getCredential failed", e)
                    callback.onFailure(e)
                }
            }
        )
    }

    private fun firebaseAuthWithGoogle(idToken: String, callback: AuthRepository.AuthCallback) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        callback.onSuccess(user)
                    } else {
                        callback.onFailure(Exception("Auth success but user is null"))
                    }
                } else {
                    callback.onFailure(task.exception ?: Exception("Auth failed"))
                }
            }
    }

    override fun signOut() { firebaseAuth.signOut() }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override fun isUserAuthenticated(): Boolean = firebaseAuth.currentUser != null
}



