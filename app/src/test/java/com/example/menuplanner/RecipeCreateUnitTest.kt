package com.example.menuplanner

import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.ui.screens.recipes.create.RecipeCreateViewModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeCreateUnitTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MenuRepository>(relaxed = true)
    private lateinit var viewModel: RecipeCreateViewModel

    @Before
    fun setup() {
        viewModel = RecipeCreateViewModel(repository)
    }

    @Test
    fun `saveRecipe calls repository with correct mapped data`() = runTest {
        // Act
        viewModel.saveRecipe(
            title = "Omelette",
            calories = 300,
            prepTime = 10,
            isVegetarian = true
        )

        // Assert
        coVerify {
            repository.saveRecipe(match {
                it.title == "Omelette" &&
                        it.calories == 300 &&
                        it.prepTimeMinutes == 10 &&
                        it.isVegetarian
            })
        }
    }
}