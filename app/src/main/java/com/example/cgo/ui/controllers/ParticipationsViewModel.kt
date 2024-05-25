package com.example.cgo.ui.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cgo.data.database.entities.Participation
import com.example.cgo.data.repositories.ParticipationsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ParticipationsState(val participations: List<Participation>)

class ParticipationsViewModel(private val repository: ParticipationsRepository) : ViewModel() {
    val state = repository.participations.map { ParticipationsState(participations = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ParticipationsState(emptyList())
    )

    fun addParticipation(participation: Participation) = viewModelScope.launch { repository.upsert(participation) }
    fun deleteParticipation(participation: Participation) = viewModelScope.launch { repository.delete(participation) }
}