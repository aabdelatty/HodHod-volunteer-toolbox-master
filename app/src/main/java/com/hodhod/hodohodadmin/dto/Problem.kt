package com.hodhod.hodohodadmin.dto

import androidx.annotation.DrawableRes
import com.hodhod.hodohodadmin.R


enum class Problems(val value: String, @DrawableRes val icon: Int, val index: Int) {
    MedicalAssistance("Medical assistance", R.drawable.ic_health, 0),
    FoodDistributors("Food distributors", R.drawable.ic_food, 1),
    WaterIssues("Water Issues", R.drawable.ic_water, 2),
    SurvivorsHandling("Survivors handling", R.drawable.ic_people, 3),
    RoadAndBridgeFixes("Road and Bridge fixes", R.drawable.ic_road, 4),
    CleanupOperations("Cleanup operations", R.drawable.ic_recycle, 5);

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

data class Problem(val count: Int = 0, val type: Problems, val parentage: Int = 0)

fun getProblems(): List<Problem> {
    return listOf(
            Problem(type = Problems.MedicalAssistance),
            Problem(type = Problems.FoodDistributors),
            Problem(type = Problems.WaterIssues),
            Problem(type = Problems.SurvivorsHandling),
            Problem(type = Problems.RoadAndBridgeFixes),
            Problem(type = Problems.CleanupOperations)
    )
}