package com.fortitude.shamsulkarim.ieltsfordory.data.database.firebase

import android.content.Context
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.DatabaseRepository
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase

class FirebaseDatabaseRepository(private val context: Context) : DatabaseRepository {
    private val reference = FirebaseDatabase.getInstance().reference

    override fun updateUserData(userId: String, data: Any, listener: OnCompleteListener<Void>?) {
        if (userId.isNotEmpty() && ConnectivityHelper.isConnectedToNetwork(context)) {
            if (listener != null) {
                reference.child(userId).setValue(data).addOnCompleteListener(listener)
            } else {
                reference.child(userId).setValue(data)
            }
        }
    }

    override fun addChildEventListener(userId: String, listener: ChildEventListener) {
        if (userId.isNotEmpty()) {
            reference.child(userId).addChildEventListener(listener)
        }
    }

    override fun removeChildEventListener(userId: String, listener: ChildEventListener) {
        if (userId.isNotEmpty()) {
            reference.child(userId).removeEventListener(listener)
        }
    }
}

