package com.example.cgo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cgo.data.database.daos.EventDAO
import com.example.cgo.data.database.daos.ParticipationDAO
import com.example.cgo.data.database.daos.UserDAO
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.Participation
import com.example.cgo.data.database.entities.User

@Database(entities = [Event::class, User::class, Participation::class], version = 5)
abstract class CGODatabase : RoomDatabase() {
    abstract fun eventDAO(): EventDAO
    abstract fun userDAO(): UserDAO
    abstract fun participationDAO(): ParticipationDAO
}