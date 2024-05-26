package com.example.cgo.data.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithEvents(
    @Embedded
    val user: User,

    @Relation(
        parentColumn = "userId",
        entityColumn = "eventId",
        associateBy = Junction(Participation::class)
    )
    val events: List<Event>,

    @Relation(
        parentColumn = "userId",
        entityColumn = "eventCreatorId"
    )
    val createdEvents: List<Event>,

    @Relation(
        parentColumn = "userId",
        entityColumn = "winnerId"
    )
    val wonEvents: List<Event>
)