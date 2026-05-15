package com.example.menuplanner

import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.MealPlan
import com.example.menuplanner.domain.model.Recipe
import com.example.menuplanner.ui.screens.mealPlans.update.MealPlanUpdateViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import java.util.UUID

class MealPlanUpdateUnitTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: MenuRepository
    private lateinit var viewModel: MealPlanUpdateViewModel

    private val mealPlanId = UUID.randomUUID()
    private val emptyMealPlan = MealPlan(
        id = mealPlanId,
        dayOfWeek = "Tuesday",
        breakfast = null,
        lunch = null,
        dinner = null,
        isCooked = false
    )

    private val mockRecipe = Recipe(
        id = UUID.randomUUID(),
        title = "Oatmeal",
        description = "Boil oats",
        calories = 300,
        prepTimeMinutes = 10,
        isVegetarian = true,
        dateAdded = Date()
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        viewModel = MealPlanUpdateViewModel(repository)
    }

    @Test
    fun `updateMealPlan aggregates recipes and saves to repository`() = runTest {
        // Load meal plan state
        coEvery { repository.getMealPlan(mealPlanId) } returns emptyMealPlan
        viewModel.loadMealPlan(mealPlanId.toString())

        // Act: Update meal plan with same recipe for all meal types
        viewModel.updateMealPlan(
            breakfast = mockRecipe,
            lunch = mockRecipe,
            dinner = mockRecipe
        )

        // Assert
        coVerify {
            repository.saveMealPlan(match {
                it.id == mealPlanId &&
                        it.breakfast?.id == mockRecipe.id &&
                        it.lunch?.id == mockRecipe.id &&
                        it.dinner?.id == mockRecipe.id
            })
        }
    }
}