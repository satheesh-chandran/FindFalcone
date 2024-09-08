package com.step.krm.findfalcone

import retrofit2.http.GET

interface FalconeService {
    @GET("planets")
    suspend fun getAllPlanets(): List<Planet>

    @GET("vehicles")
    suspend fun getAllVehicles(): List<Vehicle>
}
