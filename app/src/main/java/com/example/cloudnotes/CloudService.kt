package com.example.cloudnotes

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.view.View

class CloudService:Service() {
    lateinit var   obj:DataHelper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return super.onStartCommand(intent, flags, startId)
        //Getting id from MainActivity
        Log.d("MYTAG","Started the service")
        obj = DataHelper.getInstance(application.applicationContext)!!
        var str=""
        val intt=intent
        if(intt!=null){
            str=intt.getStringExtra("id")!!
            Log.d("MYTAG","Got id ${str}")
        }else {
            Log.d("MYTAG","No id received in service class")
        }

        //Check if internet available
        val cm=applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected=cm.getNetworkCapabilities(cm.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        if(isConnected == true) {
            //Adding database with mail as key without gmail.com wording
            //Get old data first to append it with new data else old will be overridden

            val database =
                FirebaseDatabase.getInstance().getReference(str.substring(0, str.length - 9))
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.value
                    if (data != null && (obj.getDao()
                            .getNotes() as ArrayList<NotesEntity>).size == 0
                    ) {
                        Log.d("MYTAG","Case 1")
                        //App is reinstalled and there is some previous data in firebase
                        try {
                            var newlist = ArrayList<NotesEntity>()
                            for (i in data as ArrayList<HashMap<*, *>>) {
                                newlist.add(
                                    NotesEntity(
                                        (i.get("primaryKey") as Long).toInt(),
                                        i.get("heading") as String,
                                        i.get("content") as String
                                    )
                                )
                            }
                            //Get all data from firebase and store it in local storage
                            for (i in newlist) {
                                obj.getDao().addNote(i)
                            }
                            Log.d("MYTAG","Got data")
                            Toast.makeText(applicationContext,"Data Retrieved",Toast.LENGTH_SHORT).show()
                            MainActivity.dataSet = obj.getDao().getNotes() as ArrayList<NotesEntity>
                            MainActivity.recyclerView.adapter=NotesAdapter(MainActivity.context, dataset = MainActivity.dataSet,MainActivity())
                            MainActivity.progressBar.visibility= View.INVISIBLE
                        } catch (e: Exception) {
                            Log.d("MYTAG", "ERROR AA GYA:${e.message}")
                        }
                    }
                    //App is used first time and no data is uploaded to firebase but data is created offline
                    else if (data == null && (obj.getDao()
                            .getNotes() as ArrayList<NotesEntity>).size > 0
                    ) {
                        Log.d("MYTAG","Case 2")
                        database.setValue(obj.getDao().getNotes() as ArrayList<NotesEntity>)
                        MainActivity.progressBar.visibility= View.INVISIBLE

                    }
                    else if (data==null && (obj.getDao()
                            .getNotes() as ArrayList<NotesEntity>).size == 0 ){
                        Log.d("MYTAG","Case 3")
                        Log.d("MYTAG","App is installed and no offline data is created")
                        MainActivity.progressBar.visibility= View.INVISIBLE

                    }
                    else if (data!=null && (obj.getDao()
                            .getNotes() as ArrayList<NotesEntity>).size > 0){
                        Log.d("MYTAG","Case 4")
                        //There is some previous data and a change occurs in offline database
                        //The previous data is fetched first automatically, if any ,on downloading the app again
                        //Now if any change occurs add whole offline data to firebase
                        database.setValue(obj.getDao().getNotes() as ArrayList<NotesEntity>)
                        MainActivity.progressBar.visibility= View.INVISIBLE
                    }
                    else{
                        Log.d("MYTAG","Some unexpected condition occurs")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MYTAG", error.toString() + "")
                }
            })
        }else{
            Log.d("MYTAG","NO INTERNET CONNECTION")
        }

    return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}