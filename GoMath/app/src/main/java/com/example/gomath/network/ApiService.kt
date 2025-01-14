package com.example.gomath.network

import com.example.gomath.model.LoginRequest
import com.example.gomath.model.LoginResponse
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

object RetrofitInstance {
    private const val BASE_URL = "http://gomath.daw.inspedralbes.cat:8000/"
//    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GoMathApi by lazy {
        retrofit.create(GoMathApi::class.java)
    }
}

interface GoMathApi {
    @POST("api/userLogin") // Canviar direcció
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}