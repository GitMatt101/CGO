package com.example.cgo.data.remote

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

    suspend fun getPlaceByEventLocation(street: String, city: String): List<OSMPlace> {
        val url = "$baseUrl/search.php?street=$street&city=$city&format=json&limit=1"
        return httpClient.get(url).body()
    }
}
