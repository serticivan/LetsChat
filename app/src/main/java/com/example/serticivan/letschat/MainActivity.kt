package com.example.serticivan.letschat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        register_button_register.setOnClickListener {
            val email = email_edittext_register.text.toString()
            val password = password_edittext_register.text.toString()

            Log.d("MainActivity", "email is $email")
            Log.d("MainActivity", "password: $password")

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //firebase authentication to create user with email and password

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    //else if successful
                    Log.d("Main", "User successfully created (uid: ${it.result?.user?.uid})")

                }
                .addOnFailureListener {
                    Log.d("Main", "Failed to create user: ${it.message}")
                }

        }

        already_have_account_text_view.setOnClickListener {
            Log.d("MainActivity", "try to open login activity")
            //launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
