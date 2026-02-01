package com.example.motobridge.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.motobridge.R
import com.example.motobridge.core.RideState // Import from Core
import com.example.motobridge.viewmodel.RideViewModel
import kotlinx.coroutines.launch

class RideStateFragment : Fragment(R.layout.fragment_ride) {

    private val viewModel: RideViewModel by viewModels()

    private lateinit var tvStatus: TextView
    private lateinit var cardOffer: CardView
    private lateinit var tvDestination: TextView
    private lateinit var tvFare: TextView
    private lateinit var btnAccept: Button
    
    // OTP Section
    private lateinit var layoutOtp: LinearLayout
    private lateinit var etOtp: EditText
    private lateinit var btnSubmitOtp: Button
    
    private lateinit var layoutNavigation: View
    private lateinit var tvEta: TextView
    private lateinit var btnComplete: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Bind Views
        tvStatus = view.findViewById(R.id.tv_ride_status)
        cardOffer = view.findViewById(R.id.card_offer)
        tvDestination = view.findViewById(R.id.tv_destination)
        tvFare = view.findViewById(R.id.tv_fare)
        btnAccept = view.findViewById(R.id.btn_accept)
        
        // NOTE: We need to add OTP Layout to fragment_ride.xml first!
        // Assuming we will add it shortly.
        // For now, let's programmatically find them assuming XML update comes next.
        // Or if not present, we can't bind yet.
        // Let's create the XML update task.
        
        layoutNavigation = view.findViewById(R.id.layout_navigation)
        tvEta = view.findViewById(R.id.tv_eta)
        btnComplete = view.findViewById(R.id.btn_complete)

        // Listeners
        btnAccept.setOnClickListener { viewModel.acceptOffer() }
        
        btnComplete.setOnClickListener { viewModel.completeTrip() }

        // Observer
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rideState.collect { state -> updateUi(state) }
            }
        }
        
        viewModel.simulateOffer()
    }

    private fun updateUi(state: RideState) {
        cardOffer.visibility = View.GONE
        layoutNavigation.visibility = View.GONE
        // layoutOtp.visibility = View.GONE (To be added)
        
        when (state) {
            is RideState.Idle -> {
                tvStatus.text = "Vehicle Idle"
                tvStatus.setTextColor(resources.getColor(R.color.aaos_primary, null))
            }
            is RideState.OfferReceived -> {
                tvStatus.text = "New Offer"
                cardOffer.visibility = View.VISIBLE
                tvDestination.text = "To: ${state.dropoffLocation}"
                tvFare.text = "${state.fare}"
            }
            is RideState.WaitingForOtp -> {
                tvStatus.text = "Enter OTP"
                 // Show OTP UI (Need to update XML)
            }
            is RideState.RideAccepted -> { // Deprecated in new flow? No, used intermediate
                tvStatus.text = "Heading to Pickup..."
            }
            is RideState.InRide -> {
                tvStatus.text = "In Ride"
                layoutNavigation.visibility = View.VISIBLE
                tvEta.text = "Trip: ${(state.progress * 100).toInt()}%"
                tvStatus.setTextColor(resources.getColor(R.color.aaos_accent, null))
            }
            is RideState.Completed -> {
                tvStatus.text = "Ride Completed"
            }
        }
    }
}
