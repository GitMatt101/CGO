package com.example.cgo.data.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

enum class PrivacyType {
    PUBLIC,
    FRIENDS_ONLY,
    PRIVATE
}

@Entity
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int = 0,

    @ColumnInfo
    val title: String,

    @ColumnInfo
    val description: String,

    @ColumnInfo
    val date: Date,

    @ColumnInfo
    val location: String,

    @ColumnInfo
    val maxParticipants: Int,

    @ColumnInfo
    val privacyType: PrivacyType,

    @ColumnInfo
    val eventCreatorId: Int,
)

@Entity()
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    @ColumnInfo
    val username: String,

    @ColumnInfo
    val email: String,

    @ColumnInfo
    val password: String,
)

data class UserWithEvents(
    @Embedded
    val user: User,

    @Relation(
        parentColumn = "userId",
        entityColumn = "eventCreatorId"
    )
    val events: List<Event>
)