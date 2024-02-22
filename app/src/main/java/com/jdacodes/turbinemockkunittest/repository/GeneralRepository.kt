package com.jdacodes.turbinemockkunittest.repository

import com.jdacodes.turbinemockkunittest.base.RequestResult
import com.jdacodes.turbinemockkunittest.mapped.LoginResult
import com.jdacodes.turbinemockkunittest.mapped.PostResult
import com.jdacodes.turbinemockkunittest.mapped.UserResult
import com.jdacodes.turbinemockkunittest.network.ApiService
import javax.inject.Inject

class GeneralRepository @Inject constructor(
    private val apiService: ApiService
){

    suspend fun login(credentials: String?): RequestResult<LoginResult> = try {
        val request = apiService.login(credentials)
        RequestResult.Success(
            data = LoginResult(
                apiKey = "test"
            )
        )
    } catch (e: Exception){
        RequestResult.Error(exception = e)
    }

    suspend fun users(): RequestResult<List<UserResult>> = try {
        val request = apiService.users()

        val userList:MutableList<UserResult> = mutableListOf()

        request.forEach {
            userList.add(
                UserResult(
                    id = it.id,
                    name = it.name,
                    avatarUrl = it.avatar.thumbnail
                )
            )
        }
        RequestResult.Success(
            data = userList
        )

    } catch (e: Exception){
        RequestResult.Error(exception = e)
    }

    suspend fun posts(): RequestResult<List<PostResult>> = try {
        val request = apiService.posts()

        val userList:MutableList<PostResult> = mutableListOf()

        request.forEach {
            userList.add(
                PostResult(
                    id = it.id,
                    title = it.title,
                    body = it.body,
                    userId = it.userId
                )
            )
        }
        RequestResult.Success(
            data = userList
        )

    } catch (e: Exception){
        RequestResult.Error(exception = e)
    }
}