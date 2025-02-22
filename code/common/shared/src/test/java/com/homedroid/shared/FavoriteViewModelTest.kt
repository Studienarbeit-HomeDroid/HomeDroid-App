package com.homedroid.shared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.homedroid.app.presentation.FavoriteViewModel
import com.homedroid.data.interfaces.IFavoriteRepository
import com.homedroid.data.model.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class FavoriteViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var favoriteRepository: IFavoriteRepository

    private lateinit var viewModel: FavoriteViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        favoriteRepository = mock(IFavoriteRepository::class.java)
        Dispatchers.setMain(dispatcher)
        viewModel = FavoriteViewModel(favoriteRepository)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @org.junit.Test
    fun loadFavorites_isCorrect(): Unit = runTest {
        val expectedFavorites = listOf(Device.StatusDevice("1"), Device.ActionDevice("2"))
        `when`(favoriteRepository.getFavorites()).thenReturn(expectedFavorites)
        viewModel.loadFavorites()
        advanceUntilIdle()
        assertEquals(expectedFavorites, viewModel.favorites.value)
        verify(favoriteRepository, times(2)).getFavorites()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @org.junit.Test
    fun addFavorite_isCorrect(): Unit = runTest {
        val newFavorite = Device.StatusDevice("3")
        val expectedFavorites = listOf(newFavorite)

        `when`(favoriteRepository.getFavorites()).thenReturn(expectedFavorites)

        viewModel.addFavorite(newFavorite)
        advanceUntilIdle()

        verify(favoriteRepository).addFavorite(newFavorite)
        assertEquals(expectedFavorites, viewModel.favorites.value)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @org.junit.Test
    fun removeFavorite_isCorrect(): Unit = runTest {
        val itemToRemove = Device.StatusDevice("3")
        val expectedFavorites = mutableListOf(Device.StatusDevice("3"), Device.ActionDevice("2"))
        expectedFavorites.remove(itemToRemove)

        `when`(favoriteRepository.getFavorites()).thenReturn(expectedFavorites)

        viewModel.removeFavorite(itemToRemove)
        advanceUntilIdle()

        verify(favoriteRepository).removeFavorite(itemToRemove)
        assertEquals(expectedFavorites, viewModel.favorites.value)
    }

}