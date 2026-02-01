package com.example.motobridge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motobridge.data.SafetyRepository
import com.example.vehicle.MockVehicleDataSource
import kotlinx.coroutines.flow.StateFlow

class CarStatusViewModel : ViewModel() {
    
    private val safetyRepository = SafetyRepository(viewModelScope)
    private val mockVehicle = MockVehicleDataSource()

    val isRestricted: StateFlow<Boolean> = safetyRepository.isInteractionRestricted
    val currentVehicleState = safetyRepository.vehicleState

    fun toggleSimulation() {
        mockVehicle.startSimulation()
    }
    
    fun setSpeed(kms: Float) {
        mockVehicle.setSpeed(kms)
    }

    override fun onCleared() {
        super.onCleared()
        mockVehicle.stopSimulation()
    }
}
