package com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase

import android.app.Activity
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.AuthRepository

class SignInUseCase(private val repository: AuthRepository) {
    fun execute(activity: Activity, callback: AuthRepository.AuthCallback) = repository.signIn(activity, callback)
}

