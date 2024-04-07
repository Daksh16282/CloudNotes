package com.example.cloudnotes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDAO {

    @Insert
       fun addNote(data:NotesEntity)

    @Update
      fun updateNote(data: NotesEntity)

    @Delete
      fun deleteNote(data: NotesEntity)

    @Query("SELECT * FROM  NotesData")
      fun getNotes():List<NotesEntity>
}