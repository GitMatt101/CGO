package com.example.cgo

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.cgo.data.database.CGODatabase
import com.example.cgo.data.remote.OSMDataSource
import com.example.cgo.data.repositories.EventsRepository
import com.example.cgo.data.repositories.AppRepository
import com.example.cgo.data.repositories.ParticipationsRepository
import com.example.cgo.data.repositories.UsersRepository
import com.example.cgo.ui.controllers.AppViewModel
import com.example.cgo.ui.controllers.UsersViewModel
import com.example.cgo.ui.screens.login.LoginViewModel
import com.example.cgo.ui.screens.registration.RegistrationViewModel
import com.example.cgo.ui.controllers.EventsViewModel
import com.example.cgo.ui.controllers.ParticipationsViewModel
import com.example.cgo.ui.screens.addevent.AddEventViewModel
import com.example.cgo.ui.screens.rankings.RankingsViewModel
import com.example.cgo.ui.screens.settings.changeprofile.EditProfileViewModel
import com.example.cgo.utils.LocationService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.datastore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().datastore }
    single { AppRepository(get()) }

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
        )
    }
    single {
        ParticipationsRepository(
            get<CGODatabase>().participationDAO()
        )
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single { OSMDataSource(get()) }
    single { LocationService(get()) }

    // Screens view models
    viewModel { RegistrationViewModel() }
    viewModel { LoginViewModel() }
    viewModel { AddEventViewModel() }
    viewModel { EditProfileViewModel() }
    viewModel { RankingsViewModel() }

    // Database entities view models
    viewModel { UsersViewModel(get()) }
    viewModel { EventsViewModel(get()) }
    viewModel { ParticipationsViewModel(get()) }
    viewModel { AppViewModel(get()) }
}