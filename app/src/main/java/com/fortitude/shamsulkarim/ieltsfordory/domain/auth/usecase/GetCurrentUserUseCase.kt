package com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    fun execute() = repository.getCurrentUser()
}

