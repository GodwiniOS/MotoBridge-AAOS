package com.example.motobridge.aaos

import com.example.motobridge.core.PlatformPolicy
import com.example.vehicle.VehicleDataManager

/**
 * AAOS implementation of the policy.
 * Bridges the gap between Core Logic and Vehicle Hal/Safety APIs.
 */
class AaosPlatformPolicy : PlatformPolicy {

    override fun canInteract(): Boolean {
        // Strict Rule: If speed > 0, interaction is BLOCKED.
        // We read from our Vehicle HAL layer.
        return !VehicleDataManager.isMoving()
    }

    override fun isGlanceMode(): Boolean {
        // If moving, we must show simplified UI
        return VehicleDataManager.isMoving()
    }

    override fun isBatteryOptimized(): Boolean {
        // AAOS often runs on constrained hardware or needs to save battery when ignition OFF.
        return !VehicleDataManager.isIgnitionOn()
    }
}
