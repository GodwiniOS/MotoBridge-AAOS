package com.example.driversim

import com.example.motobridge.core.PlatformPolicy

class MobilePlatformPolicy : PlatformPolicy {
    
    // Mobile/Tablet has no driving restrictions
    override fun canInteract(): Boolean = true

    // Mobile UI is always rich
    override fun isGlanceMode(): Boolean = false

    // Simplify battery logic for sim
    override fun isBatteryOptimized(): Boolean = false
}
