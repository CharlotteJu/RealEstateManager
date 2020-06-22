package com.openclassrooms.realestatemanager.models

import android.provider.ContactsContract
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "estate_agent")
class EstateAgent (@PrimaryKey @ColumnInfo(name = "last_name") var lastName : String,
                    @ColumnInfo(name = "first_name") var firstName : String,
                    @ColumnInfo (name = "email") var email: String?,
                    @ColumnInfo (name = "phone_number") var phoneNumber : String?)
{}