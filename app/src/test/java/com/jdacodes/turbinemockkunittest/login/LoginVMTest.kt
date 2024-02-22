package com.jdacodes.turbinemockkunittest.login

import app.cash.turbine.test
import com.jdacodes.turbinemockkunittest.base.ButtonState
import com.jdacodes.turbinemockkunittest.base.DataState
import com.jdacodes.turbinemockkunittest.usecase.LoginBase64UseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginVMTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @MockK
    lateinit var loginBase64UseCase: LoginBase64UseCase

    private lateinit var viewModel: LoginVM

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    private fun produceViewModel(viewState: LoginState = LoginState()): LoginVM {
        val viewModel = LoginVM(loginBase64UseCase = loginBase64UseCase)
        viewModel.viewState.value = viewState
        return viewModel
    }

    private fun getCurrentViewState() = viewModel.viewState.value

    @Test
    fun `when OnNameChange is triggered with non-empty value`() {
        val fakeValue = "123"
        viewModel = produceViewModel(
            viewState = LoginState()
        )

        viewModel.onTriggerEvent(LoginEvent.OnNameChange(newValue = fakeValue))

        assertEquals(fakeValue, getCurrentViewState().username)
    }

    @Test
    fun `when OnPasswordChange is triggered with non-empty value`() {
        val fakeValue = "123"
        viewModel = produceViewModel(
            viewState = LoginState()
        )

        viewModel.onTriggerEvent(LoginEvent.OnPasswordChange(newValue = fakeValue))

        assertEquals(fakeValue, getCurrentViewState().password)
    }

    @Test
    fun `when OnLoginClick is triggered with user and pass`() = runTest {
        //setup
        //Create fake username and password strings to be used for creating a ViewModel's UI State
        val fakeUsername = "user"
        val fakePassword = "pass"
        //Setup slots for capturing the value from the ViewModel's UI State
        val emailSlot = slot<String>()
        val passwordSlot = slot<String>()
        //set a rule when this use-case's execute function executes with captured values using slot
        //returns empty flow
        coEvery {
            loginBase64UseCase.execute(capture(emailSlot), capture(passwordSlot))
        } returns flowOf()
        //initialize the ViewModel with viewState and pass fake username and password
        viewModel = produceViewModel(
            viewState = LoginState(
                username = fakeUsername,
                password = fakePassword
            )
        )
        //actually calling the function
        viewModel.onTriggerEvent(LoginEvent.OnLoginClick)
        //verify if the function actually ran
        coVerify { loginBase64UseCase.execute(any(), any()) }
        //get captured value from the slot and assign to variables
        val email = emailSlot.captured
        val pass = passwordSlot.captured
        //assert if fake credentials is equal to the captured credentials
        assertEquals(fakeUsername, email)
        assertEquals(fakePassword, pass)
    }

    @Test
    fun `when OnLoginClick is triggered and loginInteractor emits Loading`() = runTest {
        // setup
        // when use-case's function execute is called with any username and password value
        //returns a flow DataState's Loading data class
        coEvery {
            loginBase64UseCase.execute(any(), any())
        } returns flowOf(DataState.Loading())
        //setups the viewModel with the helper function with viewState as LoginState default fields
        viewModel = produceViewModel(
            viewState = LoginState()
        )
        //call onTriggerEvent with an event of LoginEvent's OnLoginClick
        viewModel.onTriggerEvent(LoginEvent.OnLoginClick)
        //assert if the current view state's ButtonState is actually ButtonState.Loading
        assertEquals(ButtonState.Loading, getCurrentViewState().continueButtonState)
    }

    @Test
    fun `when OnLoginClick is triggered and loginInteractor emits Data`() = runTest {
        //same setup from the previous function above
        coEvery {
            loginBase64UseCase.execute(any(), any())
        } returns flowOf(DataState.Data())

        viewModel = produceViewModel(
            viewState = LoginState()
        )
        //launch in the background when onTriggerEvent is called with event of OnLoginClick object
        backgroundScope.launch {
            viewModel.onTriggerEvent(LoginEvent.OnLoginClick)
        }
        //Test the sharedFlow navigation event
        viewModel.navigationEventFlow.test {
            //await asynchronously the item from the shared flow and
            //assert if it emits a login navigation event of NavigationToHome
            val navigationEvent = awaitItem()
            assertTrue(navigationEvent is LoginNavigationEvent.NavigateToHome)
            //before exiting the test cancel and consume remaining events
            cancelAndConsumeRemainingEvents()
        }
    }
}