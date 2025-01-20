package com.example.gomath.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_login")
data class UserSession(
    @PrimaryKey val email: String,
    @ColumnInfo(name = "role") val role: String
)