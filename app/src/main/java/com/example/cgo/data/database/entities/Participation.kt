package com.example.cgo.data.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["userId", "eventId"])
data class Participation(
    val userId: Int,
    val eventId: Int
)