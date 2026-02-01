package com.example.motobridge.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.motobridge.R
import com.example.motobridge.viewmodel.CarStatusViewModel
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private val carViewModel: CarStatusViewModel by viewModels()
    
    private lateinit var safetyOverlay: FrameLayout
    private lateinit var btnSimulate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        safetyOverlay = findViewById(R.id.safety_overlay_container)
        btnSimulate = findViewById(R.id.btn_simulate_drive)

        // Add Safety Overlay
        if (savedInstanceState == null) {
             supportFragmentManager.beginTransaction()
                .replace(R.id.safety_overlay_container, SafetyOverlayFragment())
                .commit()
        }

        btnSimulate.setOnClickListener {
            carViewModel.toggleSimulation()
        }

        // Observe Safety State
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                carViewModel.isRestricted.collect { restricted ->
                    if (restricted) {
                        showSafetyOverlay()
                    } else {
                        hideSafetyOverlay()
                    }
                }
            }
        }
    }

    private fun showSafetyOverlay() {
        if (safetyOverlay.visibility != View.VISIBLE) {
            safetyOverlay.visibility = View.VISIBLE
        }
    }
    
    private fun hideSafetyOverlay() {
        if (safetyOverlay.visibility != View.GONE) {
            safetyOverlay.visibility = View.GONE
        }
    }
}
