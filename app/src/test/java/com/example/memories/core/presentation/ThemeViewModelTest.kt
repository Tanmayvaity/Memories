package com.example.memories.core.presentation

import com.example.memories.core.domain.usecase.GetThemeUseCase
import com.example.memories.core.domain.usecase.SetThemeUseCase
import com.example.memories.core.domain.usecase.ThemeUseCase
import com.example.memories.feature.feature_other.presentation.ThemeTypes
import com.example.memories.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ThemeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getThemeUseCase = mockk<GetThemeUseCase>()
    private val setThemeUseCase = mockk<SetThemeUseCase>(relaxed = true)

    private fun viewModel() = ThemeViewModel(ThemeUseCase(getThemeUseCase, setThemeUseCase))

    @Test
    fun init_mapsDarkModeTrueToDark() {
        every { getThemeUseCase() } returns flowOf(true)

        val vm = viewModel()

        assertEquals(ThemeTypes.DARK, vm.isDarkModeEnabled.value)
    }

    @Test
    fun init_mapsDarkModeFalseToLight() {
        every { getThemeUseCase() } returns flowOf(false)

        val vm = viewModel()

        assertEquals(ThemeTypes.LIGHT, vm.isDarkModeEnabled.value)
    }

    @Test
    fun changeThemeType_setsStateDirectly() {
        every { getThemeUseCase() } returns flowOf(false)
        val vm = viewModel()

        vm.onEvent(ThemeEvents.ChangeThemeType(ThemeTypes.SYSTEM))

        assertEquals(ThemeTypes.SYSTEM, vm.isDarkModeEnabled.value)
    }

    @Test
    fun setTheme_whenSystem_doesNotPersist() = runTest {
        every { getThemeUseCase() } returns flowOf(false)
        val vm = viewModel()
        vm.onEvent(ThemeEvents.ChangeThemeType(ThemeTypes.SYSTEM))

        vm.onEvent(ThemeEvents.SetTheme)

        coVerify(exactly = 0) { setThemeUseCase(any()) }
    }

    @Test
    fun setTheme_whenDark_persistsTrue() = runTest {
        every { getThemeUseCase() } returns flowOf(false)
        val vm = viewModel()
        vm.onEvent(ThemeEvents.ChangeThemeType(ThemeTypes.DARK))

        vm.onEvent(ThemeEvents.SetTheme)

        coVerify { setThemeUseCase(true) }
    }

    @Test
    fun setTheme_whenLight_persistsFalse() = runTest {
        every { getThemeUseCase() } returns flowOf(false)
        val vm = viewModel()
        vm.onEvent(ThemeEvents.ChangeThemeType(ThemeTypes.LIGHT))

        vm.onEvent(ThemeEvents.SetTheme)

        coVerify { setThemeUseCase(false) }
    }
}
