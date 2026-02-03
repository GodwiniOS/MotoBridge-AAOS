package com.example.driversim

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.motobridge.core.RestrictedActionException
import com.example.motobridge.core.RideState
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnAction: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tv_status)
        btnAction = findViewById(R.id.btn_action)

        val app = application as DriverSimApp
        val manager = app.rideManager

        lifecycleScope.launch {
            manager.rideState.collect { state ->
                updateUi(state)
            }
        }
        
        btnAction.setOnClickListener {
            val state = manager.rideState.value
            when (state) {
                is RideState.Idle -> manager.simulateOffer()
                is RideState.OfferReceived -> manager.acceptOffer()
                is RideState.WaitingForOtp -> {
                    // Mobile can always input OTP
                    try {
                        if (manager.submitOtp("1234")) {
                            Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        }
                    } catch (ex: RestrictedActionException) {
                        Toast.makeText(this, "OTP blocked by policy", Toast.LENGTH_SHORT).show()
                    }
                }
                is RideState.RideAccepted -> {}
                is RideState.InRide -> manager.completeRide()
                is RideState.Completed -> {}
            }
        }
    }

    private fun updateUi(state: RideState) {
        when (state) {
            is RideState.Idle -> {
                tvStatus.text = "Driver Idle"
                btnAction.text = "Wait for Offer..."
            }
            is RideState.OfferReceived -> {
                tvStatus.text = "New Request: Rs 120"
                btnAction.text = "Accept Ride"
            }
            is RideState.WaitingForOtp -> {
                tvStatus.text = "Waiting for OTP (1234)"
                btnAction.text = "Auto-Fill OTP"
            }
            is RideState.RideAccepted -> {
                tvStatus.text = "Ride Started..."
                btnAction.isEnabled = false
            }
            is RideState.InRide -> {
                tvStatus.text = "In Ride: ${(state.progress*100).toInt()}%"
                btnAction.text = "End Ride"
                btnAction.isEnabled = true
            }
            is RideState.Completed -> {
                tvStatus.text = "Ride Finished"
                btnAction.isEnabled = false
            }
        }
    }
}
