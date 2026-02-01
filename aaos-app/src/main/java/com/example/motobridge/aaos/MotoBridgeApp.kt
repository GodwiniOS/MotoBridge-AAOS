package com.example.motobridge.aaos

import android.app.Application
import com.example.motobridge.core.RideManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MotoBridgeApp : Application() {

    // Simple Manual Dependency Injection
    // In a real app complexity, use Hilt/Dagger.
    
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    lateinit var rideManager: RideManager
        private set

    override fun onCreate() {
        super.onCreate()
        
        // 1. Create the Policy Implementation
        val policy = AaosPlatformPolicy()
        
        // 2. Inject into Core
        rideManager = RideManager(policy, applicationScope)
    }
}
