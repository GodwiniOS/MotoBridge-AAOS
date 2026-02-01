package com.example.driversim

import android.app.Application
import com.example.motobridge.core.RideManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class DriverSimApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var rideManager: RideManager
        private set

    override fun onCreate() {
        super.onCreate()
        val policy = MobilePlatformPolicy()
        rideManager = RideManager(policy, applicationScope)
    }
}
