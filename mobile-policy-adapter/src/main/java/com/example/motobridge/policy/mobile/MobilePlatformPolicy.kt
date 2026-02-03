package com.example.motobridge.policy.mobile

import com.example.motobridge.core.PlatformPolicy
import com.example.motobridge.core.UserAction

/**
 * Mobile policy adapter: always allow interactions.
 */
class MobilePlatformPolicy : PlatformPolicy {
    override fun canInteract(action: UserAction): Boolean = true
}
