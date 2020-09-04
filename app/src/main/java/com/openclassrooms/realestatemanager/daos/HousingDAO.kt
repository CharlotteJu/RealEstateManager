package com.openclassrooms.realestatemanager.daos

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.models.*

/**
 * Interface DAO for [Housing] with [RoomDatabase]
 */
@Dao
interface HousingDAO {
    @Query("SELECT * FROM housing")
    fun getAllHousing(): LiveData<List<Housing>>

    @Query("SELECT * FROM housing WHERE reference =:reference")
    fun getHousing(reference: String): LiveData<Housing>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createHousing(housing: Housing): Long

    @Update
    suspend fun updateHousing(housing: Housing): Int

    @Delete
    suspend fun deleteHousing(housing: Housing)

    @Transaction
    @Query("SELECT * FROM housing WHERE reference = :reference")
    fun getCompleteHousing(reference: String): LiveData<CompleteHousing>


   @Transaction
    @Query("""
        SELECT DISTINCT(h.reference), h.type, h.area, h.price, h.rooms, h.bedrooms, h.bathrooms, 
                        h.state, h.dateEntry, h.dateSale, h.lastUpdate, poi.poi_type, a.country, a.city, 
                        ea.estate_agent_name, a.housing_reference, poi.housing_reference,
                        ea.housing_reference, count(ph.housing_reference) cnt
        FROM housing h
        LEFT JOIN address a ON h.reference == a.housing_reference
        LEFT JOIN housing_poi poi ON h.reference == poi.housing_reference
        LEFT JOIN housing_estate_agent ea ON h.reference == ea.housing_reference
        LEFT JOIN photo ph ON h.reference == ph.housing_reference
        /* LEFT JOIN (SELECT photo.housing_reference as ref, count(photo.housing_reference) as nbPhoto FROM housing LEFT JOIN photo ON housing.reference == photo.housing_reference) as pho ON h.reference == pho.ref*/
        WHERE
        (:type IS NULL OR h.type = :type)
        AND (:priceLower IS NULL OR h.price BETWEEN :priceLower AND :priceHigher)
        AND (:areaLower IS NULL OR h.area BETWEEN :areaLower AND :areaHigher)
        AND (:roomLower IS NULL OR h.rooms BETWEEN :roomLower AND :roomHigher)
        AND (:bedRoomLower IS NULL OR h.bedrooms BETWEEN :bedRoomLower AND :bedRoomHigher)
        AND (:bathRoomLower IS NULL OR h.bathrooms BETWEEN :bathRoomLower AND :bathRoomHigher)
        AND (:state IS NULL OR h.state = :state)
        AND (:dateEntry IS NULL OR dateEntry >= :dateEntry)/*DateDiff("day", :dateEntry, dateEntry) >= //date(dateEntry) >= date(:dateEntry))*/
        AND (:dateSale IS NULL OR dateSale >= :dateSale)
        AND (:typePoi IS NULL OR poi.poi_type = :typePoi)
        AND (:city IS NULL OR a.city LIKE lower(:city))
        AND (:country IS NULL OR a.country = :country)
        AND (:estateAgent IS NULL OR ea.estate_agent_name = :estateAgent)
        /*AND (:numberPhotos IS NULL OR pho.nbPhoto > :numberPhotos) */
        GROUP BY h.reference
        HAVING (:numberPhotos IS NULL OR cnt >= :numberPhotos)
        /*AND cnt >= :numberPhotos  TODO : Voir pour listPhoto*/
        """)
    fun getListCompleteHousingFilter(type : String? = null,
                                     priceLower : Double? = 0.0,
                                     priceHigher : Double? = Double.MAX_VALUE,
                                     areaLower : Double? = 0.0,
                                     areaHigher : Double? = Double.MAX_VALUE,
                                     roomLower : Int? = 0,
                                     roomHigher : Int? = Int.MAX_VALUE,
                                     bedRoomLower : Int? = 0,
                                     bedRoomHigher : Int? = Int.MAX_VALUE,
                                     bathRoomLower : Int? = 0,
                                     bathRoomHigher : Int? = Int.MAX_VALUE,
                                     state : String? = null,
                                     dateEntry : Long? = null,
                                     dateSale : Long? = null,
                                     city : String? = null,
                                     country : String? = null,
                                     typePoi : String? = null,
                                     estateAgent : String? = null,
                                     numberPhotos : Int? = null): LiveData<List<CompleteHousing>>


    //CURSOR

    @Transaction
    @Query("SELECT * FROM housing")
    fun getAllCompleteHousing(): LiveData<List<CompleteHousing>>

    @Query("SELECT * FROM housing")
    fun getAllHousingWithCursor() : Cursor

    @Query("SELECT * FROM housing WHERE reference =:reference")
    fun getHousingWithCursor(reference: String) : Cursor

    @Query("DELETE FROM housing WHERE reference =:reference")
    suspend fun deleteHousingWithCursor (reference: String)

}

