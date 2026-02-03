package com.example.motobridge.core

/**
 * High-level user actions that may be restricted by OEM policy.
 */
enum class UserAction {
    OTP_INPUT,
    TEXT_ENTRY,
    MANUAL_ACTION,
    NAVIGATION
}

/**
 * Core policy interface. Implemented by platform adapters.
 * The Core never reads vehicle state directly.
 */
interface PlatformPolicy {
    fun canInteract(action: UserAction): Boolean
}

/**
 * Fail-fast exception thrown when a restricted action is attempted.
 */
class RestrictedActionException(
    val action: UserAction,
    message: String = "Action $action is restricted by platform policy."
) : RuntimeException(message)
