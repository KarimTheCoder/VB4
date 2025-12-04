package com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.database.DatabaseRepository
import com.google.firebase.database.ChildEventListener

class AddChildEventListenerUseCase(private val repository: DatabaseRepository) {
    fun execute(userId: String, listener: ChildEventListener) {
        repository.addChildEventListener(userId, listener)
    }
}

