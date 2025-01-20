package com.example.gomath.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gomath.model.User
import com.example.gomath.model.UserSession

@Dao
interface GoMathDao {
    @Insert
    suspend fun insertUser(user: UserSession)

    @Query("SELECT * FROM user_login LIMIT 1")
    suspend fun getUser(): UserSession?

    @Query("DELETE FROM user_login")
    suspend fun deleteUser()
}