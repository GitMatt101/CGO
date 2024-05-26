package com.example.cgo.data.remote

import com.example.cgo.utils.Coordinates
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OSMPlace(
    @SerialName("place_id")
    val id: Int,
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double,
    @SerialName("display_name")
    val displayName: String
)

class OSMDataSource(
    private val httpClient: HttpClient
) {
    private val baseUrl = "https://nominatim.openstreetmap.org"

    suspend fun searchPlaces(query: String): List<OSMPlace> {
        val url = "$baseUrl/?q=$query&format=json&limit=1"
        return httpClient.get(url).body()
    }

    suspend fun getPlace(coordinates: Coordinates): OSMPlace {
        val url = "$baseUrl/reverse?lat=${coordinates.latitude}&lon=${coordinates.longitude}&format=json&limit=1"
        return httpClient.get(url).body()
    }
}
