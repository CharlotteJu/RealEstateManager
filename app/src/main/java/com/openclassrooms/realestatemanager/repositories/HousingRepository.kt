package com.openclassrooms.realestatemanager.repositories

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.daos.HousingDAO
import com.openclassrooms.realestatemanager.models.Housing

class HousingRepository(private val housingDao : HousingDAO)
{

    fun getAllHousing() : LiveData<List<Housing>> = this.housingDao.getAllHousing()

    fun getHousing(reference : String) : LiveData<Housing> = this.housingDao.getHousing(reference)

    suspend fun createHousing(housing: Housing) = this.housingDao.createHousing(housing)

    suspend fun updateHousing (housing: Housing) = this.housingDao.updateHousing(housing)

}