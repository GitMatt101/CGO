package com.example.cgo.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithUsers(
    @Embedded
    val event: Event,

    @Relation(
        parentColumn = "eventId",
        entityColumn = "participantId"
    )
    val participants: List<User>
)