package com.example.vehicle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Simulates vehicle behavior for testing/demo purposes.
 * This effectively acts as the "Vehicle HAL" simulator.
 */
class MockVehicleDataSource {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var isSimulationRunning = false

    fun startSimulation() {
        if (isSimulationRunning) return
        isSimulationRunning = true
        
        scope.launch {
            // Initial state: Parked, Ignition OFF
            VehicleDataManager.updateState { 
                it.copy(
                    ignition = IgnitionState(IgnitionState.State.OFF),
                    gear = VehicleGear.PARK,
                    speed = VehicleSpeed(0f, false),
                    parkingBrakeOn = true
                )
            }
            delay(2000)

            // 1. Ignition ON
            VehicleDataManager.updateState {
                it.copy(ignition = IgnitionState(IgnitionState.State.ON))
            }
            delay(1000)

            // 2. Mock Drive Loop
            while (isSimulationRunning) {
                // Stop -> Start
                simulateDriveCycle()
                delay(5000) 
            }
        }
    }

    fun stopSimulation() {
        isSimulationRunning = false
    }

    fun setSpeed(speedKmph: Float) {
        VehicleDataManager.updateState {
            it.copy(
                speed = VehicleSpeed(speedKmph, speedKmph > 0),
                // Auto-update gear logic for simplicity
                gear = if (speedKmph > 0) VehicleGear.DRIVE else VehicleGear.NEUTRAL,
                parkingBrakeOn = speedKmph == 0f
            )
        }
    }
    
    private suspend fun simulateDriveCycle() {
        // Accelerate
        for (i in 0..40 step 5) {
            if (!isSimulationRunning) return
            setSpeed(i.toFloat())
            delay(500)
        }
        
        // Cruise
        delay(2000)
        
        // Decelerate
        for (i in 40 downTo 0 step 10) {
            if (!isSimulationRunning) return
            setSpeed(i.toFloat())
            delay(500)
        }
    }
}
