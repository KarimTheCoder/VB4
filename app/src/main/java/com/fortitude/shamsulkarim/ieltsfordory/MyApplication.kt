package com.fortitude.shamsulkarim.ieltsfordory

import android.app.Application
import android.util.Log
import com.fortitude.shamsulkarim.ieltsfordory.data.database.migration.LegacyMigrationHelper
import com.fortitude.shamsulkarim.ieltsfordory.data.database.migration.MigrationResult
import com.fortitude.shamsulkarim.ieltsfordory.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
        
        // Trigger legacy database migration on startup
        val migrationHelper: LegacyMigrationHelper by inject()
        applicationScope.launch(Dispatchers.IO) {
            when (val result = migrationHelper.migrateIfNeeded()) {
                is MigrationResult.Success -> {
                    Log.d("VB4", "Migration success: ${result.wordsMigrated} words, ${result.sessionsMigrated} sessions")
                }
                is MigrationResult.Failed -> {
                    Log.e("VB4", "Migration failed: ${result.error}")
                }
                is MigrationResult.AlreadyMigrated -> {
                    Log.d("VB4", "Database already migrated")
                }
            }
        }
    }
}

