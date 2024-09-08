package com.step.krm.findfalcone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FalconeViewModel : ViewModel() {
    var _vehicles: MutableLiveData<List<Vehicle>> = MutableLiveData()
    var vehicles: LiveData<List<Vehicle>> = _vehicles
        private set
    var _planets: MutableLiveData<List<Planet>> = MutableLiveData()
    var planets: LiveData<List<Planet>> = _planets
        private set

    init {
        fetchPlanets()
        fetchVehicles()
    }

    fun fetchPlanets() {
        viewModelScope.launch {
            try {
                val planetsResult: List<Planet> = RetrofitInstance.service.getAllPlanets()
                _planets.value = planetsResult
            } catch (_: Exception) {

            }
        }
    }

    fun fetchVehicles() {
        viewModelScope.launch {
            try {
                val vehiclesResult: List<Vehicle> = RetrofitInstance.service.getAllVehicles()
                _vehicles.value = vehiclesResult
            } catch (_: Exception) {

            }
        }
    }
}