package com.example.cloudnotes

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignIn : AppCompatActivity() {
    lateinit var preferences:SharedPreferences
    companion object {
        var user = FirebaseAuth.getInstance().currentUser
    }
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sign_in)
            //Preference to preserve mail id so that it doesn't become empty in onStart
            preferences =
                getSharedPreferences("MailId", MODE_PRIVATE)

        //If already logged in then directly move to the next one
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("id", preferences.getString("sharedId", ""))
            startActivity(intent)
            finish()
        }
            val signIn = findViewById<Button>(R.id.signIn)
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_id)).requestEmail().build();
            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            val auth = Firebase.auth
            val getResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                        if (task.isSuccessful) {
                            Log.d("MYTAG", "Success")
                            try {
                                val googleSignInAccount = task.result
                                if (googleSignInAccount != null) {
                                    val editor = preferences.edit()
                                    editor.putString("sharedId", googleSignInAccount.email)
                                    editor.apply() // or editor.commit();
                                    var credentials = GoogleAuthProvider.getCredential(
                                        googleSignInAccount.idToken,
                                        null
                                    )
                                    auth.signInWithCredential(credentials)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d("MYTAG", "Authenticated")
                                                val intent = Intent(this, MainActivity::class.java)
                                                intent.putExtra("id", googleSignInAccount.email)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Log.d("MYTAG", "Not Authenticated")
                                            }
                                        }
                                }
                            } catch (e: Exception) {
                                Log.d("MYTAG", "$e")
                            }
                        } else {
                            Log.d("MYTAG", "FAILS")
                        }
                    } else {
                        Log.d("MYTAG", "Result not ok")
                    }
                }
            signIn.setOnClickListener {
                val intent = googleSignInClient.signInIntent
                getResult.launch(intent)
                Log.d("MYTAG", "Called")
            }
        }


}