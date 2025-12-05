package com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.ConnectivityRepository

class IsConnectedUseCase(private val repository: ConnectivityRepository) {
    fun execute(): Boolean = repository.isConnected()
}

