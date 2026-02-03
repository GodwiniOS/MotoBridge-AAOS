package com.example.motobridge.core

/**
 * Pure Kotlin representation of a Ride.
 * No Android dependencies here.
 */
sealed class RideState {
    object Idle : RideState()
    
    data class OfferReceived(
        val offerId: String,
        val pickupLocation: String,
        val dropoffLocation: String,
        val fare: String,
        val distance: String
    ) : RideState()
    
    data class RideAccepted(val offerId: String) : RideState()
    
    // OTP State is critical for safety checks (typing blocked while moving)
    data class WaitingForOtp(val offerId: String) : RideState()
    
    data class InRide(val progress: Float) : RideState() // 0.0 to 1.0
    
    object Completed : RideState()
}
