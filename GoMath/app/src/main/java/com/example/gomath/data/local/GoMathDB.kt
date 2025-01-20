package com.example.gomath.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gomath.model.UserSession

@Database(entities = [UserSession::class], version = 1)
abstract class GoMathDB : RoomDatabase() {
    abstract fun GoMathDao(): GoMathDao

    companion object {
        @Volatile
        private var INSTANCE: GoMathDB? = null

        fun getDatabase(context: Context): GoMathDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoMathDB::class.java,
                    "GoMath"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
