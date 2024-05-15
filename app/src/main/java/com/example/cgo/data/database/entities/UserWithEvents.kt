package com.example.cgo.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithEvents(
    @Embedded
    val user: User,

    @Relation(
        parentColumn = "userId",
        entityColumn = "eventCreatorId"
    )
    val events: List<Event>
)