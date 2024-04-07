package com.example.cloudnotes

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.example.cloudnotes.R.*
import com.example.cloudnotes.R.layout.activity_write_note
import kotlinx.coroutines.launch


class WriteNote : AppCompatActivity() {
    private lateinit var heading:EditText
    private lateinit var content:EditText
   lateinit var obj: DataHelper
    var pk:Int=0
    var position:Int=0
   //Static flag variable to keep track whether the note is edited or not
   companion object{
        var flag=0
   }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_write_note)

        obj=DataHelper.getInstance(this)!!
        heading=findViewById(id.heading)
        content=findViewById(id.content)


        //Setting data received from intent
        var intent=intent
        if(intent!=null){
            if(intent.extras!=null){
                var dataHead=intent.getStringExtra("notesHead")
                var dataContent = intent.getStringExtra("notesContent")
                pk=intent.getIntExtra("notesPrimaryKey",0)
                position=intent.getIntExtra("noteposition",0)
                heading.setText(dataHead)
                content.setText(dataContent)

                //Coming from Intent that is update data if changes occur instead of inserting new note
                flag=1
            }
        }

    }



    //Note saving on clicking back i.e. without the need of any done or ok button
    override fun onPause() {
        super.onPause()

        //If nothing enters,then discard the note
        if (heading.text.toString().equals("") && content.text.toString().equals("")){
            Toast.makeText(applicationContext,"Empty Note Discarded",Toast.LENGTH_SHORT).show()
        }
        //Add data pair to dataSet
        else{
                if(flag==1){
                    obj?.getDao()?.updateNote(NotesEntity(pk,heading.text.toString(), content.text.toString()))
                    flag=0
                }else {
                    obj?.getDao()?.addNote(NotesEntity(heading.text.toString(), content.text.toString()))

                }
        }
    }
}