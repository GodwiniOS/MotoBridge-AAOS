package com.example.motobridge.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * The Brains of the application.
 * Manages the ride state machine and enforces platform policies.
 */
class RideManager(
    private val policy: PlatformPolicy,
    private val scope: CoroutineScope
) {

    private val _rideState = MutableStateFlow<RideState>(RideState.Idle)
    val rideState: StateFlow<RideState> = _rideState.asStateFlow()

    // Simulate receiving an offer from backend
    fun simulateOffer() {
        if (_rideState.value is RideState.Idle) {
            _rideState.value = RideState.OfferReceived(
                offerId = UUID.randomUUID().toString(),
                pickupLocation = "Central Metro Station",
                dropoffLocation = "Tech Park, Building 4",
                fare = "₹120",
                distance = "4.5 km"
            )
        }
    }

    fun acceptOffer() {
        // Anyone can accept a ride (single tap)
        val currentState = _rideState.value
        if (currentState is RideState.OfferReceived) {
            _rideState.value = RideState.WaitingForOtp(currentState.offerId)
        }
    }

    fun submitOtp(otp: String): Boolean {
        // Enforce Policy: Cannot type OTP if interaction is blocked
        if (!policy.canInteract()) {
            return false // Action denied by platform
        }

        // Simulating OTP validation
        if (otp == "1234") {
            _rideState.value = RideState.RideAccepted((_rideState.value as RideState.WaitingForOtp).offerId)
            startRide()
            return true
        }
        return false
    }

    private fun startRide() {
        _rideState.value = RideState.InRide(0f)
        
        // Simulate ride progress
        scope.launch(Dispatchers.Default) {
            var p = 0f
            while (p < 1.0f) {
                // If ride was cancelled or finished externally
                if (_rideState.value !is RideState.InRide) break
                
                // Throttle updates if battery optimized
                val delayTime = if (policy.isBatteryOptimized()) 2000L else 1000L
                delay(delayTime)
                
                p += 0.05f
                if (p >= 1.0f) {
                    _rideState.value = RideState.Completed
                } else {
                    _rideState.value = RideState.InRide(p)
                }
            }
        }
    }

    fun completeRide() {
        _rideState.value = RideState.Completed
        scope.launch {
            delay(3000)
            _rideState.value = RideState.Idle
        }
    }
}
