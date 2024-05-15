package com.example.cgo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cgo.data.database.DAOs.EventDAO
import com.example.cgo.data.database.DAOs.UserDAO
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.User

@Database(entities = [Event::class, User::class], version = 1)
abstract class CGODatabase : RoomDatabase() {
    abstract fun eventDAO(): EventDAO
    abstract fun userDAO(): UserDAO
}