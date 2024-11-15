package com.jdacodes.turbinemockkunittest.usecase

import android.util.Base64
import com.jdacodes.turbinemockkunittest.base.DataState
import com.jdacodes.turbinemockkunittest.base.ProgressBarState
import com.jdacodes.turbinemockkunittest.base.RequestResult
import com.jdacodes.turbinemockkunittest.repository.GeneralRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginBase64UseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {
    fun execute(
        username: String?,
        password: String?
    ): Flow<DataState<out Any>> = flow {

        emit(DataState.Loading(progressState = ProgressBarState.Loading))

        val credentials = getCredentials (username, password)

        val loginResult = generalRepository.login(credentials)
        when (loginResult) {
            is RequestResult.Success -> {
                emit(DataState.Data(data = loginResult.data))
            }
            is RequestResult.Error -> {
                emit(DataState.Error(error = loginResult.exception))
            }
        }

    }

    fun getCredentials(
        username: String?,
        password: String?
    ):String? {
        val credentials = if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)
        } else {
            null
        }

        return credentials
    }
}