package com.example.motobridge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.motobridge.aaos.MotoBridgeApp
import com.example.motobridge.core.RideState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RideViewModel(application: Application) : AndroidViewModel(application) {

    private val rideManager = getApplication<MotoBridgeApp>().rideManager

    val rideState: StateFlow<RideState> = rideManager.rideState

    fun simulateOffer() {
        viewModelScope.launch {
            delay(1000)
            rideManager.simulateOffer()
        }
    }

    fun acceptOffer() {
        rideManager.acceptOffer()
    }
    
    // OTP Submission - Returns Boolean success/fail
    fun submitOtp(otp: String): Boolean {
        return rideManager.submitOtp(otp)
    }

    fun completeTrip() {
        rideManager.completeRide()
    }
}
