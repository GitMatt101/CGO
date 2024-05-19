package com.example.cgo

import androidx.room.Room
import com.example.cgo.data.database.CGODatabase
import com.example.cgo.data.repositories.EventsRepository
import com.example.cgo.data.repositories.UsersRepository
import com.example.cgo.ui.controllers.EventsViewModel
import com.example.cgo.ui.screens.addevent.AddEventViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            CGODatabase::class.java,
            "cgo"
        ).build()
    }

    single {
        EventsRepository(
            get<CGODatabase>().eventDAO(),
        )
    }
    single {
        UsersRepository(
            get<CGODatabase>().userDAO(),
            // Probabilmente serve per gestire le immagini
            // get<Context>().applicationContext.contentResolver
        )
    }

    viewModel { EventsViewModel(get()) }

    viewModel { AddEventViewModel() }
}