package com.example.cgo

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cgo.data.database.CGODatabase
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.PrivacyType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventTest() {
    private lateinit var database: CGODatabase

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            context,
            CGODatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() {
        if (this::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun insertEvent() = runBlocking {
        val event = Event(
            1,
            "Evento",
            "Descrizione",
            "2022-12-31",
            "12:00",
            "Casa di matti",
            4,
            PrivacyType.PUBLIC,
            1
        )
        database.eventDAO().upsert(event)
        println(event)
        val retrievedEvent = database.eventDAO().getAll().first().firstOrNull()
        Assert.assertEquals(event, retrievedEvent)
    }
}