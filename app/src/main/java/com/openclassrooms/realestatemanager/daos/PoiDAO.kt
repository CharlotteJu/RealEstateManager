package com.openclassrooms.realestatemanager.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.models.Housing
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Poi

@Dao
interface PoiDAO
{
    @Query("SELECT * FROM poi")
    fun getAllPoi() : LiveData<List<Poi>>

    @Query("SELECT * FROM poi WHERE type =:type")
    fun getPoi(type : String) : LiveData<Poi>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createPoi(poi: Poi) : Long

    @Update
    fun updatePoi(poi: Poi) : Int
}