package com.example.cgo.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val date: String,

    @ColumnInfo
    val location: String,

    @ColumnInfo
    val maxParticipants: Int,

    @ColumnInfo
    val privacyType: PrivacyType,

    @ColumnInfo
    val eventCreatorId: Int,
)
