package com.example.menuplanner.data

import com.example.menuplanner.data.model.MealPlan
import java.util.UUID
import java.util.Date
import java.time.LocalDate

import com.example.menuplanner.data.model.Recipe

object DummyData {
    val recipes = listOf(
        Recipe(UUID.randomUUID(),
            "Spaghetti Bolognese",
            426,
            45,
            false,
            Date()),
        Recipe(UUID.randomUUID(),
            "Avocado Toast",
            195,
            10,
            true,
            Date()),
        Recipe(UUID.randomUUID(),
            "Chicken Curry",
            625,
            60,
            false,
            Date()),
        Recipe(UUID.randomUUID(),
            "Fried Eggs with Sausages",
            250,
            20,
            false,
            Date()),
    )

    val mealPlans = listOf(
        MealPlan(UUID.randomUUID(),
            "Monday",
            recipes[1],
            recipes[0],
            recipes[2],
            false),
        MealPlan(UUID.randomUUID(),
            "Tuesday",
            recipes[3],
            recipes[0],
            recipes[2],
            false),
        MealPlan(UUID.randomUUID(),
            "Wednesday",
            recipes[1],
            recipes[0],
            recipes[2],
            false),
    )
}