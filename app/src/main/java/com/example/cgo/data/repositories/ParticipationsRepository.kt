package com.example.cgo.data.repositories

import com.example.cgo.data.database.daos.ParticipationDAO
import com.example.cgo.data.database.entities.Participation
import kotlinx.coroutines.flow.Flow

class ParticipationsRepository(
    private val participationDAO: ParticipationDAO
) {
    val participations: Flow<List<Participation>> = participationDAO.getAll()

    suspend fun upsert(participation: Participation) = participationDAO.upsert(participation)
    suspend fun delete(participation: Participation) = participationDAO.delete(participation)
}