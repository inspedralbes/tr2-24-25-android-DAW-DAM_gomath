package com.example.gomath.model

data class User (
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val points: Int = 0,
    val questions: Int = 0
)

data class Users(
    var members: List<User> = emptyList()
)

data class LoginResponse (
    val email: String = "",
    val role: String = ""
)

data class LoginRequest (
    val email: String,
    val password: String
)
