package com.fortitude.shamsulkarim.ieltsfordory.domain.database

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ChildEventListener

interface DatabaseRepository {
    fun updateUserData(userId: String, data: Any, listener: OnCompleteListener<Void>?)
    fun addChildEventListener(userId: String, listener: ChildEventListener)
    fun removeChildEventListener(userId: String, listener: ChildEventListener)
}



