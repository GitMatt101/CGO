package com.example.cgo

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.cgo.data.database.CGODatabase
import com.example.cgo.data.repositories.EventsRepository
import com.example.cgo.data.repositories.SettingsRepository
import com.example.cgo.data.repositories.UsersRepository
import com.example.cgo.ui.controllers.UsersViewModel
import com.example.cgo.ui.screens.login.LoginViewModel
import com.example.cgo.ui.screens.registration.RegistrationViewModel
import com.example.cgo.ui.controllers.EventsViewModel
import com.example.cgo.ui.screens.addevent.AddEventViewModel
import com.example.cgo.ui.screens.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.datastore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().datastore }
    single { SettingsRepository(get()) }

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

    // Screens view models
    viewModel { RegistrationViewModel() }
    viewModel { LoginViewModel() }
    viewModel { AddEventViewModel() }
    viewModel { SettingsViewModel(get()) }

    // Database entities view models
    viewModel { UsersViewModel(get()) }
    viewModel { EventsViewModel(get()) }
}