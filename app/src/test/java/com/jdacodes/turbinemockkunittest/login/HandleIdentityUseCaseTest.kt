package com.jdacodes.turbinemockkunittest.login

import android.util.Base64
import app.cash.turbine.test
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import com.jdacodes.turbinemockkunittest.base.DataState
import com.jdacodes.turbinemockkunittest.base.RequestResult
import com.jdacodes.turbinemockkunittest.mapped.LoginResult
import com.jdacodes.turbinemockkunittest.repository.GeneralRepository
import com.jdacodes.turbinemockkunittest.usecase.LoginBase64UseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HandleIdentityUseCaseTest {

    @MockK
    private lateinit var generalRepository: GeneralRepository

    private lateinit var useCase: LoginBase64UseCase

    private val loginSuccessResult = LoginResult("apikey")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    private fun produceUseCase() = LoginBase64UseCase(
        generalRepository = generalRepository
    )

    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`() {
        mockkStatic(Base64::class) //Base64 is static by itself
        val arraySlot = slot<ByteArray>() //value for byte array

        every { //everytime this is called
            Base64.encodeToString(
                capture(arraySlot),
                Base64.DEFAULT
            ) //Android sdk function, don't know about it
        } answers { //call this instead
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured) //use it
        }

        val stringSlot = slot<String>() //value for string
        every { //everytime this is called
            Base64.decode(capture(stringSlot), Base64.DEFAULT)
        } answers { //call this instead
            java.util.Base64.getDecoder().decode(stringSlot.captured)//use it
        }
    }

    @Test
    fun `execute login check if credentials was null`() = runTest {
        val email = null
        val password = null
        //set rule when login function is called inside a coroutine
        //returns a Success with LoginResult object with apiKey:String as parameter
        coEvery {
            generalRepository.login(null)
        } returns RequestResult.Success(loginSuccessResult)

        useCase = produceUseCase()
        //test the returned flow from execute function using turbine with the passed email and pw
        turbineScope {
            useCase.execute(email, password).testIn(backgroundScope)
        }

        //check if function ran and if credential parameter is null
        coVerify { generalRepository.login(isNull()) }
    }

    @Test
    fun `execute login check if we have passed credentials`() = runTest {
        val email = "email"
        val password = "pass"
        val credentials = "credentials"
        //set a rule when login is executed in a coroutine with any parameter
        //return Success with LoginResult
        coEvery {
            generalRepository.login(any())
        } returns RequestResult.Success(loginSuccessResult)
        // initialize a useCase object
        useCase = produceUseCase()
        //when getCredentials with passed email and password strings
        //returns credentials string
        every {
            useCase.getCredentials(email, password)
        } returns credentials
        //Inside the turbineScope, run and test the useCase's execute function
        //that returns a Flow in the background
        turbineScope {
            useCase.execute(email, password).testIn(backgroundScope)
        }
        //check by running login function in repository
        //if it is not null
        coVerify { generalRepository.login(isNull(inverse = true)) }
    }

    @Test
    fun `execute login without credentials and return data`() = runTest {

        //setup
        val email = null
        val password = null
        //set whenever login function with null credentials is called
        // return Success of LoginResult object with apiKey of "test" string
        coEvery {
            generalRepository.login(null)
        } returns RequestResult.Success(LoginResult(apiKey = "test"))
        //initialize useCase with the helper function
        useCase = produceUseCase()
        //Test
        //In turbineScope, run the execute function and pass email and password which are both null
        // test every emission of the flow which is returned from the execute()
        turbineScope {
            useCase.execute(email, password).test {
                val firstResult = awaitItem() //get the first emission
                assertTrue(firstResult is DataState.Loading) // assert if type is Loading

                val secondResult = awaitItem() //get the second emission
                assertTrue(secondResult is DataState.Data) //assert if type is Data
                //wait until there is no more emission and then exit the test
                awaitComplete()
            }
        }

    }

    @Test
    fun `execute login without credentials and return error`() = runTest {
        //setup fake email and password
        val email = null
        val password = null
        //setup fake Exception and Initialize with Exception object
        val fakeException = Exception()
        //whenever login with null credentials return Error with fakeException as parameter
        coEvery {
            generalRepository.login(null)
        } returns RequestResult.Error(fakeException)
        //Initialize the useCase with helper function
        useCase = produceUseCase()
        //run the execute function with fake email and password as parameters then assign to flow variable
        val flow = useCase.execute(email, password)
        //test the flow
        flow.test {
            //check the first emission by waiting for the item
            //check if it true that the first result is type Loading
            val firstResult = awaitItem()
            assertTrue(firstResult is DataState.Loading)
            //check the second emission by waiting for the item
            //check if it true that the second result is type Error
            val secondResult = awaitItem()
            assertTrue(secondResult is DataState.Error)
            //assert if the fake exception is equal to error field in DataState.Error
            assertEquals(fakeException, (secondResult as DataState.Error).error)
            awaitComplete() //wait all remaining emissions and disregard before exiting
        }
    }

}