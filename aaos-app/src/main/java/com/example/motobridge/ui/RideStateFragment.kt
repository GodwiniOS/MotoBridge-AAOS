package com.example.motobridge.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.motobridge.R
import com.example.motobridge.core.RestrictedActionException
import com.example.motobridge.core.RideState // Import from Core
import com.example.motobridge.viewmodel.CarStatusViewModel
import com.example.motobridge.viewmodel.RideViewModel
import kotlinx.coroutines.launch

class RideStateFragment : Fragment(R.layout.fragment_ride) {

    private val viewModel: RideViewModel by viewModels()
    private val carViewModel: CarStatusViewModel by activityViewModels()

    private lateinit var tvStatus: TextView
    private lateinit var cardOffer: CardView
    private lateinit var tvDestination: TextView
    private lateinit var tvFare: TextView
    private lateinit var btnAccept: Button
    
    // OTP Section
    private lateinit var layoutOtp: LinearLayout
    private lateinit var etOtp: EditText
    private lateinit var btnSubmitOtp: Button
    private lateinit var tvOtpRestriction: TextView
    
    private lateinit var layoutNavigation: View
    private lateinit var tvEta: TextView
    private lateinit var btnComplete: Button

    private var isInteractionRestricted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Bind Views
        tvStatus = view.findViewById(R.id.tv_ride_status)
        cardOffer = view.findViewById(R.id.card_offer)
        tvDestination = view.findViewById(R.id.tv_destination)
        tvFare = view.findViewById(R.id.tv_fare)
        btnAccept = view.findViewById(R.id.btn_accept)
        
        layoutOtp = view.findViewById(R.id.layout_otp)
        etOtp = view.findViewById(R.id.et_otp)
        btnSubmitOtp = view.findViewById(R.id.btn_submit_otp)
        tvOtpRestriction = view.findViewById(R.id.tv_otp_restriction)
        
        layoutNavigation = view.findViewById(R.id.layout_navigation)
        tvEta = view.findViewById(R.id.tv_eta)
        btnComplete = view.findViewById(R.id.btn_complete)

        // Listeners
        btnAccept.setOnClickListener { viewModel.acceptOffer() }
        
        btnComplete.setOnClickListener { viewModel.completeTrip() }

        btnSubmitOtp.setOnClickListener {
            val otp = etOtp.text.toString().trim()
            if (otp.isEmpty()) {
                Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val success = viewModel.submitOtp(otp)
                if (success) {
                    Toast.makeText(requireContext(), "OTP Verified", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: RestrictedActionException) {
                Toast.makeText(requireContext(), "OTP blocked while moving", Toast.LENGTH_SHORT).show()
            }
        }

        // Observer
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.rideState.collect { state -> updateUi(state) }
                }
                launch {
                    carViewModel.isRestricted.collect { restricted ->
                        isInteractionRestricted = restricted
                        updateOtpRestriction(restricted)
                    }
                }
            }
        }
        
        viewModel.simulateOffer()
    }

    private fun updateUi(state: RideState) {
        cardOffer.visibility = View.GONE
        layoutNavigation.visibility = View.GONE
        layoutOtp.visibility = View.GONE
        
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
                layoutOtp.visibility = View.VISIBLE
                updateOtpRestriction(isInteractionRestricted)
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

    private fun updateOtpRestriction(restricted: Boolean) {
        val enabled = !restricted
        etOtp.isEnabled = enabled
        btnSubmitOtp.isEnabled = enabled
        tvOtpRestriction.visibility = if (restricted) View.VISIBLE else View.GONE
    }
}
