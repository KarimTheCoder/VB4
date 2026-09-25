package com.fortitude.shamsulkarim.ieltsfordory.testutil

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences

class FakeContext(private val sp: SharedPreferences) : ContextWrapper(null) {
    override fun getApplicationContext(): Context = this
    override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences = sp
}

object TestContextFactory {
    fun createContext(sharedPreferences: SharedPreferences = FakeSharedPreferences()): Context {
        return FakeContext(sharedPreferences)
    }
}
