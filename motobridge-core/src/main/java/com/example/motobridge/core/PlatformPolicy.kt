package com.example.motobridge.core

/**
 * Interface that each platform (Mobile vs AAOS) must implement.
 * The Core logic uses this to decide if an action is allowed.
 */
interface PlatformPolicy {
    
    /**
     * Can the user interact with complex UI elements (like typing OTP)?
     * Mobile: Always true.
     * AAOS: False if moving.
     */
    fun canInteract(): Boolean
    
    /**
     * Should the app reduce animations/updates?
     */
    fun isGlanceMode(): Boolean
    
    /**
     * Returns true if we should throttle GPS/Network to save battery.
     */
    fun isBatteryOptimized(): Boolean
}
