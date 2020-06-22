package com.openclassrooms.realestatemanager.database

import android.content.Context
import android.os.strictmode.InstanceCountViolation
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.daos.*
import com.openclassrooms.realestatemanager.models.*


@Database (entities = [Housing::class,
                        Address::class,
                        EstateAgent::class,
                        HousingEstateAgent::class,
                        HousingPoi::class,
                        Photo::class,
                        Poi::class],
            version = 1,
            exportSchema = false)

abstract class AppDatabase : RoomDatabase()
{
    abstract fun housingDao() : HousingDAO
    abstract fun addressDao() : AddressDAO
    abstract fun estateAgentDao() : EstateAgentDAO
    abstract fun photoDao() : PhotoDAO
    abstract fun poiDao() : PoiDAO
    abstract fun housingEstateAgentDao() : HousingEstateAgentDAO
    abstract fun housingPoi() : HousingPoiDAO



    companion object
    {
        @Volatile
        private var INSTANCE : AppDatabase?=null

        fun getDatabase(context:Context) : AppDatabase
        {
            val temp = this.INSTANCE
           
            if (temp != null)
            {
               return temp
            }

            synchronized(AppDatabase::class)
            {
                return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "DATABASE").build()
            }
        }
    }
}