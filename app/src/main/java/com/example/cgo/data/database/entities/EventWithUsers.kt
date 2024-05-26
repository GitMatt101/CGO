package com.example.cgo.data.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EventWithUsers(
    @Embedded
    val event: Event,

    @Relation(
        parentColumn = "eventId",
        entityColumn = "userId",
        associateBy = Junction(Participation::class)
    )
    val participants: List<User>
)