package com.fortitude.shamsulkarim.ieltsfordory.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.ConnectivityRepository

class AndroidConnectivityRepository(private val context: Context) : ConnectivityRepository {
    override fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetwork: NetworkInfo? = cm?.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}

