package com.gloushkov.dogceo.ui.main

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gloushkov.dogceo.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Mock
    private val viewModel = MainViewModel(Application())

    @Test fun mainViewModel_validateSubmitInput_ok() = runTest(UnconfinedTestDispatcher()) {
        assert(viewModel.validateSubmitInput("2") > -1)
        assert(viewModel.submitError.value == null)
    }

    @Test fun mainViewModel_validateSubmitInput_bellowRange() {
        viewModel.validateSubmitInput("-1")
        assert(viewModel.submitError.value == MainViewModel.SubmitError.BELLOW_RANGE)
    }
    @Test fun mainViewModel_validateSubmitInput_aboveRange() {
        viewModel.validateSubmitInput("11")
        assert(viewModel.submitError.value == MainViewModel.SubmitError.ABOVE_RANGE)
    }
    @Test fun mainViewModel_validateSubmitInput_notInteger() {
        viewModel.validateSubmitInput("123asd")
        assert(viewModel.submitError.value == MainViewModel.SubmitError.NOT_A_NUMBER)
    }
    @Test fun mainViewModel_validateSubmitInput_hugeNumber() {
        viewModel.validateSubmitInput("12345678901234567890")
        assert(viewModel.submitError.value == MainViewModel.SubmitError.NOT_A_NUMBER)
    }
}