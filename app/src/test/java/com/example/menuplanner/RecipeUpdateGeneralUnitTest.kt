package com.example.menuplanner

import app.cash.turbine.test
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.Recipe
import com.example.menuplanner.ui.screens.recipes.update.RecipeUpdateViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeUpdateGeneralUnitTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MenuRepository>(relaxed = true)
    private lateinit var viewModel: RecipeUpdateViewModel

    private val testId = UUID.randomUUID()
    private val baseRecipe = Recipe(
        id = testId,
        title = "Old Title",
        description = "Old Desc",
        calories = 100,
        prepTimeMinutes = 5,
        isVegetarian = false,
        dateAdded = Date()
    )

    @Before
    fun setup() {
        viewModel = RecipeUpdateViewModel(repository)
    }

    @Test
    fun `loadRecipe updates the state with recipe from repository`() = runTest {
        coEvery { repository.getRecipe(testId) } returns baseRecipe

        viewModel.loadRecipe(testId.toString())

        viewModel.recipe.test {
            assertEquals(baseRecipe, awaitItem())
        }
    }

    @Test
    fun `updateRecipe calls save with modified fields while preserving ID`() = runTest {
        coEvery { repository.getRecipe(testId) } returns baseRecipe
        viewModel.loadRecipe(testId.toString())

        // Act
        viewModel.updateRecipe(
            title = "New Title",
            calories = 500,
            prepTime = 30,
            isVegetarian = true
        )

        // Assert
        coVerify {
            repository.saveRecipe(match {
                it.id == testId && // ID must remain the same
                        it.title == "New Title" &&
                        it.calories == 500 &&
                        it.isVegetarian
            })
        }
    }

    @Test
    fun `updateRecipe does nothing if no recipe is loaded`() = runTest {
        viewModel.updateRecipe("Title", 100, 10, true)
        coVerify(exactly = 0) { repository.saveRecipe(any()) }
    }
}