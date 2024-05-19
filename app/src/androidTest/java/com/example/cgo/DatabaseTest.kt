package com.example.cgo

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cgo.data.database.CGODatabase
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.PrivacyType
import com.example.cgo.data.database.entities.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
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
    fun insertUser() = runBlocking {
        val user = User(1, "matti", "matti@gmail.com", "password", null, 1)
        database.userDAO().upsert(user)
        println(user)
        val retrievedUser = database.userDAO().getAll().first().firstOrNull()
        assertEquals(user, retrievedUser)
    }
}

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
        assertEquals(event, retrievedEvent)
    }
}