package com.example.teamval.domain.repository

import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun saveProfile(profile: PlayerProfile)
    suspend fun saveAppearance(appearance: AppAppearance)
}
