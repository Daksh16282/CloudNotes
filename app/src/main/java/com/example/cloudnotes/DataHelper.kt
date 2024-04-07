package com.example.cloudnotes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities =[NotesEntity::class], exportSchema = false, version = 1)
abstract  class DataHelper : RoomDatabase() {

//Making elements static so that they can be accessed using classname
    companion object {
        val DATABASE_NAME="NOTES DB"

        var reference:DataHelper?=null

        fun getInstance(context: Context): DataHelper? {
            synchronized(this) {
                if (reference == null) {
                    reference = Room.databaseBuilder(context, DataHelper::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration().allowMainThreadQueries().build()
                }
            }
            return reference
        }
    }

    abstract fun getDao():NotesDAO

}