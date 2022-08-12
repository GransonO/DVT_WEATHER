package com.granson.dvtweather.data.db.dao

import androidx.room.*
import com.granson.dvtweather.data.db.entities.Places
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace

@Dao
interface WeatherDao {
    @Query("SELECT * FROM places")
    suspend fun getAllPlaces(): List<SavedPlace>

    @Update(entity = Places::class)
    suspend fun updatePlace(place: SavedPlace): Int

    @Insert(entity = Places::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: SavedPlace): Long

    @Delete(entity = Places::class)
    suspend fun deletePlace(place: SavedPlace): Int
}