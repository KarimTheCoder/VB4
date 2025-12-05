package com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.AuthRepository

class IsUserAuthenticatedUseCase(private val repository: AuthRepository) {
    fun execute() = repository.isUserAuthenticated()
}

