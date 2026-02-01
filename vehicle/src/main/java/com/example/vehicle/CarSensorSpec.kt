package com.example.vehicle

/**
 * Data classes representing vehicle signals.
 */

data class VehicleSpeed(
    val speedKmph: Float,
    val isMoving: Boolean
)

enum class VehicleGear {
    PARK,
    NEUTRAL,
    DRIVE,
    REVERSE
}

data class IgnitionState(
    val state: State
) {
    enum class State {
        OFF,
        ACC,
        ON,
        START
    }
}

/**
 * Combined state of the vehicle relevant for UI Safety.
 */
data class VehicleState(
    val speed: VehicleSpeed = VehicleSpeed(0f, false),
    val gear: VehicleGear = VehicleGear.PARK,
    val ignition: IgnitionState = IgnitionState(IgnitionState.State.OFF),
    val parkingBrakeOn: Boolean = true
)
