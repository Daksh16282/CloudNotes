package com.example.cloudnotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity() : AppCompatActivity() {

    lateinit var str:String
    companion object {
         lateinit var adapter:NotesAdapter

        var dataSet:ArrayList<NotesEntity> = ArrayList()
        private lateinit var  obj:DataHelper
         lateinit var recyclerView: RecyclerView
        private lateinit var addNote: FloatingActionButton
        private lateinit var deleteNote: FloatingActionButton
        private lateinit var cancle: FloatingActionButton
        private lateinit var signOut: FloatingActionButton
        lateinit var context:Context
        lateinit var progressBar: ProgressBar
    }
    private lateinit var search:SearchView
    private var searchSet:ArrayList<NotesEntity> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context=this
        progressBar=findViewById(R.id.progressBar)
        recyclerView=findViewById(R.id.notesRecycler)
        obj=DataHelper.getInstance(this)!!
        addNote=findViewById(R.id.addNote)
        deleteNote=findViewById(R.id.deleteNote)
        cancle=findViewById(R.id.cancle)
        search=findViewById(R.id.searchView)
        signOut=findViewById(R.id.signOut)
        dataSet = obj.getDao().getNotes() as ArrayList<NotesEntity>
        if(dataSet.size>0){
            progressBar.visibility=View.INVISIBLE
        }
        //Adding Note
        addNote.setOnClickListener {
            var intent: Intent = Intent(this@MainActivity, WriteNote::class.java)
            startActivity(intent)
        }
        //Checking signin and adding data
        str=""
        val intt=intent
        if(intt!=null){
            str=intt.getStringExtra("id")!!
        }else{
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
        }
        //The user's internet may off when he previously add a note so update notes everytime when ser opens the app
    //Signing Out
        signOut.setOnClickListener {
            
            //To show the popup of accounts when user tries to login again after logging out
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_id)).requestEmail().build();
            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            googleSignInClient.signOut()

            //Actually logout the user
            FirebaseAuth.getInstance().signOut()
            Log.d("MYTAG","User:${FirebaseAuth.getInstance().currentUser}")
            val intent2 = Intent(this,SignIn::class.java)
            //Clear main activity from activity stack
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent2)

        }


        //Get data of entered notes and set adapter
        updateUI(0)
        //Setting layout of recycler view


        val layoutref = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager= layoutref
        layoutref.spanCount=2
        
        


        //Search Facility
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })


        //Deleting multiple selections
        deleteNote.setOnClickListener {
            for (i in adapter.getPKSet()){
                obj.getDao().deleteNote(NotesEntity(i))
            }
            deleteNote.visibility=View.INVISIBLE
            cancle.visibility=View.INVISIBLE
            updateUI(0)
        }
        cancle.setOnClickListener {
            deleteNote.visibility=View.INVISIBLE
            cancle.visibility=View.INVISIBLE
            adapter.setselFlag(false)
            updateUI(0)
        }
    }

    private fun filterList(newText: String?) {
        searchSet.clear()
        for (i in dataSet) {
            if (newText != null) {
                if (i.heading.lowercase()
                        .contains(newText.lowercase()) || i.content.lowercase()
                        .contains(newText.lowercase())
                ) {
                    searchSet.add(NotesEntity(i.heading, i.content))
                }
            }
        }
        if(!searchSet.isEmpty()){
            adapter.setFilteredList(searchSet)
        }
    }

    //Refreshing data to make changes done in note visible if any
     override fun onRestart() {
        super.onRestart()
        updateUI(0)
    }
    fun updateUI(flag: Int) {
        if (flag == 0) {
            dataSet = obj.getDao().getNotes() as ArrayList<NotesEntity>
            //Dataset can't be written outside else the below line will execute when service called updateUi(1) and at that time adapter can't be set as onCreate is not executed yet as service is running in bg
            Log.d("MYTAG", "Starting Service")
            callService()
            Log.d("MYTAG", "Started")
        }
        //Set data to adapter
        Log.d("MYTAG", "Updating Adapter")
        adapter = NotesAdapter(this, dataSet, MainActivity())
        recyclerView.adapter = adapter

        Log.d("MYTAG", "Updated Adapter")

    }

    private fun callService() {
        val intent = Intent(this,CloudService::class.java)
        intent.putExtra("id",str)
        startService(intent)
    }
    fun getCancle():FloatingActionButton{
        return cancle
    }
    fun getDelete():FloatingActionButton{
        return deleteNote
    }






    }




