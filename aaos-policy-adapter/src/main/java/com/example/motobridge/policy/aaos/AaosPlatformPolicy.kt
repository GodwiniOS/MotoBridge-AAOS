package com.example.motobridge.policy.aaos

import android.car.drivingstate.CarUxRestrictionsManager
import com.example.motobridge.core.PlatformPolicy
import com.example.motobridge.core.UserAction

/**
 * AAOS policy adapter backed by CarUxRestrictionsManager.
 * Falls back to a demo motion signal when running without a real car service.
 */
class AaosPlatformPolicy(
    private val uxRestrictionsManager: CarUxRestrictionsManager?,
    private val isMovingFallback: (() -> Boolean)? = null
) : PlatformPolicy {

    override fun canInteract(action: UserAction): Boolean {
        val restricted = isRestrictedByUx() || (isMovingFallback?.invoke() == true)
        if (!restricted) return true

        return when (action) {
            UserAction.OTP_INPUT,
            UserAction.TEXT_ENTRY,
            UserAction.MANUAL_ACTION -> false
            UserAction.NAVIGATION -> true
        }
    }

    private fun isRestrictedByUx(): Boolean {
        val restrictions = uxRestrictionsManager?.currentCarUxRestrictions ?: return false
        return restrictions.isRequiresDistractionOptimization
    }
}
