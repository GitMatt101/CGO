package com.example.cgo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Event::class, User::class], version = 1)
abstract class CGODatabase : RoomDatabase() {
    abstract fun eventDAO(): EventDAO
    abstract fun userDAO(): UserDAO
}