package com.example.cloudnotes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.properties.Delegates

@Entity(tableName = "NotesData")
class NotesEntity {



   @PrimaryKey(autoGenerate = true)
   var primaryKey:Int=0

    @ColumnInfo(name = "Heading")
    lateinit var heading:String
    @ColumnInfo(name = "Content")
    lateinit var content:String

    //For insertion
    constructor(heading: String, content: String) {
        this.heading = heading
        this.content = content
    }

    //For updation and deletion
    @Ignore
    constructor(primaryKey: Int, heading: String, content: String) {
        this.primaryKey = primaryKey
        this.heading = heading
        this.content = content
    }

    @Ignore
    constructor(primaryKey: Int) {
        this.primaryKey = primaryKey
    }

    //Getters and setters
    fun getHeadingValue():String{
        return this.heading
    }
    fun setHeadingValue(data:String){
        this.heading=data
    }

    fun getContentValue():String{
        return this.content
    }
    fun setContentValue(data:String){
         this.content=data
    }
}