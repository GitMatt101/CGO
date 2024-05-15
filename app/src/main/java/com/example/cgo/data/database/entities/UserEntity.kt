package com.example.cgo.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

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

    @ColumnInfo
    val profilePicture: Blob,

    @ColumnInfo
    val gamesWon: Int,
)
