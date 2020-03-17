package com.revolutan.hodhodclint.dto

import androidx.annotation.DrawableRes
import com.revolutan.hodhodclint.R
import java.util.*


data class LatLng(val latitude: Double, val longitude: Double)

data class Issue(val type: String, var reporterName: String? = null, var lat: Double? = null, var lng: Double? = null)
data class Reporter(var name: String = "Reporter", var lat: Double = 0.0, var lng: Double = 0.0, val speciality: String = "")


enum class Problems(val value: String, @DrawableRes val icon: Int) {
    MedicalAssistance("Medical assistance", R.drawable.ic_health),
    FoodDistributors("Food distributors", R.drawable.ic_food),
    WaterIssues("Water Issues", R.drawable.ic_water),
    SurvivorsHandling("Survivors handling", R.drawable.ic_people),
    RoadAndBridgeFixes("Road and Bridge fixes", R.drawable.ic_road),
    CleanupOperations("Cleanup operations", R.drawable.ic_recycle);

    companion object {
        fun fromString(str: String): Problems {
            return when (str) {
                "Medical assistance" -> MedicalAssistance
                "Food distributors" -> FoodDistributors
                "Water Issues" -> WaterIssues
                "Survivors handling" -> SurvivorsHandling
                "Road and Bridge fixes" -> RoadAndBridgeFixes
                else -> CleanupOperations

            }
        }
    }

}


fun getIssuesTypes(): List<Issue> {
    return listOf(
            Issue(type = Problems.MedicalAssistance.value),
            Issue(type = Problems.FoodDistributors.value),
            Issue(type = Problems.WaterIssues.value),
            Issue(type = Problems.SurvivorsHandling.value),
            Issue(type = Problems.RoadAndBridgeFixes.value),
            Issue(type = Problems.CleanupOperations.value)
    )
}


fun generateRandomReporter(): Reporter {
    val randomLocation =
            getLocation(31.2213, 29.9379, 20)
    val lat = randomLocation.latitude
    val lng = randomLocation.longitude
    return listOf(Reporter("Eslam", lat, lng, Problems.MedicalAssistance.value),
            Reporter("Hussein", lat, lng, Problems.FoodDistributors.value),
            Reporter("Ahmed", lat, lng, Problems.WaterIssues.value),
            Reporter("Mohamed", lat, lng, Problems.SurvivorsHandling.value),
            Reporter("Abeer", lat, lng, Problems.RoadAndBridgeFixes.value),
            Reporter("Salah", lat, lng, Problems.CleanupOperations.value),
            Reporter("Mostafa", lat, lng, Problems.MedicalAssistance.value),
            Reporter("Yahia", lat, lng, Problems.FoodDistributors.value),
            Reporter("Magdy", lat, lng, Problems.WaterIssues.value),
            Reporter("Ibrahiem", lat, lng, Problems.SurvivorsHandling.value),
            Reporter("Hend", lat, lng, Problems.RoadAndBridgeFixes.value),
            Reporter("Dina", lat, lng, Problems.CleanupOperations.value),
            Reporter("Eman", lat, lng, Problems.MedicalAssistance.value),
            Reporter("Moamn", lat, lng, Problems.FoodDistributors.value),
            Reporter("Doaa", lat, lng, Problems.WaterIssues.value)
    ).shuffled().first()
}



fun getLocation(x0: Double, y0: Double, radius: Int): LatLng {
    val random = Random()

    // Convert radius from meters to degrees
    val radiusInDegrees = (radius / 111000f).toDouble()

    val u = random.nextDouble()
    val v = random.nextDouble()
    val w = radiusInDegrees * Math.sqrt(u)
    val t = 2.0 * Math.PI * v
    val x = w * Math.cos(t)
    val y = w * Math.sin(t)

    // Adjust the x-coordinate for the shrinking of the east-west distances
    val new_x = x / Math.cos(Math.toRadians(y0))

    val foundLongitude = new_x + x0
    val foundLatitude = y + y0
    return LatLng(foundLatitude, foundLongitude)
}