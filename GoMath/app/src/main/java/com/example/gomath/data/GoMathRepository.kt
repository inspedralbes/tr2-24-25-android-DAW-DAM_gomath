package com.example.gomath.data

import com.example.gomath.model.LoginRequest
import com.example.gomath.model.LoginResponse
import com.example.gomath.model.User
import com.example.gomath.network.RetrofitInstance
import retrofit2.Response

suspend fun loginFromApi(loginRequest: LoginRequest): Result<LoginResponse> {
    return try {
        val response = RetrofitInstance.api.login(loginRequest)
        if (response.isSuccessful) {
            response.body()?.let { Result.success(it) }
                ?: Result.failure(NullPointerException("Response body is null"))
        } else {
            Result.failure(Exception("Login error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}