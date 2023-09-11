package com.gloushkov.dogceo.ui.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
class MainViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val viewModel = MainViewModel()

    @Test fun mainViewModel_validateSubmitInput_ok() {
        assertTrue(viewModel.validateSubmitInput("2"))
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