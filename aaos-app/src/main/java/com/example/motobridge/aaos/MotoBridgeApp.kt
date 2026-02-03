package com.example.motobridge.aaos

import android.app.Application
import android.car.Car
import android.car.drivingstate.CarUxRestrictionsManager
import com.example.motobridge.core.RideManager
import com.example.motobridge.policy.aaos.AaosPlatformPolicy
import com.example.vehicle.VehicleDataManager
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
        
        val carUxManager = try {
            val car = Car.createCar(this)
            car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as CarUxRestrictionsManager
        } catch (ex: Exception) {
            null
        }

        // AAOS policy adapter backed by CarUxRestrictionsManager,
        // with a demo fallback tied to the mock vehicle signals.
        val policy = AaosPlatformPolicy(carUxManager) { VehicleDataManager.isMoving() }

        rideManager = RideManager(policy, applicationScope)
    }
}
