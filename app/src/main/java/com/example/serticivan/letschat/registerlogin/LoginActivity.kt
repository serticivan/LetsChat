package com.example.serticivan.letschat.registerlogin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.serticivan.letschat.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()

            Log.d("LoginActivity", "Attempt to login with email/pw: $email/**")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        }

        back_to_registration_textview.setOnClickListener {
            finish()
        }
    }


}