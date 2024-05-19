package com.example.cgo

import androidx.room.Room
import com.example.cgo.data.database.CGODatabase
import com.example.cgo.data.repositories.EventsRepository
import com.example.cgo.data.repositories.UsersRepository
import com.example.cgo.ui.controllers.UsersViewModel
import com.example.cgo.ui.screens.login.LoginViewModel
import com.example.cgo.ui.screens.registration.RegistrationViewModel
import com.example.cgo.ui.controllers.EventsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            CGODatabase::class.java,
            "cgo"
        ).fallbackToDestructiveMigration().build()
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

    viewModel { RegistrationViewModel() }
    viewModel { LoginViewModel() }

    viewModel { UsersViewModel(get()) }
    viewModel { EventsViewModel(get()) }
}