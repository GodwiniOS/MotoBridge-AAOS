package com.example.motobridge.data

import com.example.vehicle.VehicleDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Single source of truth for safety constraints.
 * Combines vehicle data with Automotive UX restrictions.
 */
class SafetyRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    // In a real app, we would also listen to CarUxRestrictionsManager.
    // For this PoC, we derive restrictions primarily from the Vehicle HAL "IsMoving" mock.
    
    val isInteractionRestricted: StateFlow<Boolean> = VehicleDataManager.vehicleState
        .map { it.speed.isMoving }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val vehicleState = VehicleDataManager.vehicleState
}
