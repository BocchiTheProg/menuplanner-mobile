package com.example.menuplanner

import com.example.menuplanner.data.DummyData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DummyDataTest {

    @Before
    fun setup() {
        // Reset the data before each test
        DummyData.resetToDefault()
    }

    // --- Test Recipe ---

    @Test
    fun addRecipe_increasesListSize() {
        val initialSize = DummyData.recipes.size
        DummyData.addRecipe("New Test Recipe", 300, 20, true)
        assertEquals(initialSize + 1, DummyData.recipes.size)
    }

    @Test
    fun addRecipe_setsEmptyDescriptionByDefault() {
        DummyData.addRecipe("No Desc", 100, 5, false)
        val lastRecipe = DummyData.recipes.last()
        assertEquals("", lastRecipe.description)
    }

    @Test
    fun deleteRecipe_removesCorrectItem() {
        val recipeToDelete = DummyData.recipes[0]
        DummyData.recipes.remove(recipeToDelete)
        assertFalse(DummyData.recipes.contains(recipeToDelete))
    }

    // --- Test Recipe update ---

    @Test
    fun updateRecipe_updatesTitleInRecipesList() {
        val originalRecipe = DummyData.recipes[0]
        val updatedRecipe = originalRecipe.copy(title = "Updated Name")

        DummyData.updateRecipe(updatedRecipe)

        val found = DummyData.recipes.find { it.id == originalRecipe.id }
        assertEquals("Updated Name", found?.title)
    }

    @Test
    fun updateRecipe_cascadesChangesToMealPlans() {
        // Take recipe from initial list
        val avocadoToast = DummyData.recipes.find { it.title == "Avocado Toast" }!!
        val updatedAvocado = avocadoToast.copy(title = "Super Avocado Toast")

        DummyData.updateRecipe(updatedAvocado)

        // Check if name inside meal plan with this element have updated
        val planWithAvocado = DummyData.mealPlans.find { it.breakfast.id == avocadoToast.id }
        assertEquals("Super Avocado Toast", planWithAvocado?.breakfast?.title)
    }

    @Test
    fun updateRecipeDescription_doesNotChangeOtherFields() {
        val original = DummyData.recipes[0]
        val updated = original.copy(description = "New Instruction")

        DummyData.updateRecipe(updated)

        val result = DummyData.recipes.find { it.id == original.id }!!
        assertEquals("New Instruction", result.description)
        assertEquals(original.title, result.title)
        assertEquals(original.calories, result.calories)
    }

    // --- Test MealPlans logic ---

    @Test
    fun toggleMealPlanCookedStatus_changesBooleanValue() {
        val plan = DummyData.mealPlans[0]
        val initialStatus = plan.isCooked

        // Change preparation state of meal plan
        val index = DummyData.mealPlans.indexOf(plan)
        DummyData.mealPlans[index] = plan.copy(isCooked = !initialStatus)

        assertEquals(!initialStatus, DummyData.mealPlans[index].isCooked)
    }

    @Test
    fun totalCaloriesCalculation_isCorrectForSpecificPlan() {
        val plan = DummyData.mealPlans[0] // Monday plan
        val expectedCalories = plan.breakfast.calories + plan.lunch.calories + plan.dinner.calories

        // Check if calories calculation is correct
        val actualCalories = listOf(plan.breakfast, plan.lunch, plan.dinner).sumOf { it.calories }

        assertEquals(expectedCalories, actualCalories)
    }

    @Test
    fun vegetarianCheck_returnsFalseWhenMeatIncluded() {
        // Find plan with non-vegetarian recipe
        val planWithMeat = DummyData.mealPlans.find {
            !it.breakfast.isVegetarian || !it.lunch.isVegetarian || !it.dinner.isVegetarian
        }

        val isAllVegetarian = listOf(
            planWithMeat!!.breakfast,
            planWithMeat.lunch,
            planWithMeat.dinner
        ).all { it.isVegetarian }

        assertFalse(isAllVegetarian)
    }

    @Test
    fun idUniqueness_newRecipesHaveDifferentIDs() {
        DummyData.addRecipe("Recipe A", 100, 10, true)
        DummyData.addRecipe("Recipe B", 200, 20, false)

        val lastTwo = DummyData.recipes.takeLast(2)
        assertNotEquals(lastTwo[0].id, lastTwo[1].id)
    }
}