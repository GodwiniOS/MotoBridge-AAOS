package com.example.vehicle

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Singleton manager to hold the current state of vehicle signals.
 * In a real app, this would subscribe to CarPropertyManager.
 * Here, it acts as a read-only source for the app, updated by the MockDataSource.
 */
object VehicleDataManager {

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    // Internal update function for MockDataSource
    internal fun updateState(update: (VehicleState) -> VehicleState) {
        _vehicleState.update(update)
    }
    
    // Explicit read-only accessors for Java compatibility or simple checks
    fun isMoving(): Boolean {
        // For motorcycles, we assume moving if speed > 0
        return _vehicleState.value.speed.speedKmph > 0
    }

    fun isIgnitionOn(): Boolean {
        return _vehicleState.value.ignition.state == IgnitionState.State.ON
    }
}
