package com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase

import com.fortitude.shamsulkarim.ieltsfordory.domain.database.DatabaseRepository
import com.google.android.gms.tasks.OnCompleteListener

class UpdateUserDataUseCase(private val repository: DatabaseRepository) {
    fun execute(userId: String, data: Any, listener: OnCompleteListener<Void>?) {
        repository.updateUserData(userId, data, listener)
    }
}



