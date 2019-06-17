package com.example.serticivan.letschat

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }

        selectphoto_button_register.setOnClickListener {
            Log.d(TAG,"try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        already_have_account_text_view.setOnClickListener {
            Log.d(TAG, "try to open login activity")
            //launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what the selected image was
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            //to see image on button(transparent button)
            selectphoto_button_register.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }

    }

    private fun performRegister() {

        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        Log.d(TAG, "email is $email")
        Log.d(TAG, "password: $password")

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        //firebase authentication to create user with email and password

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d(TAG, "User successfully created (uid: ${it.result?.user?.uid})")

                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()

            }

    }

    private fun uploadImageToFirebaseStorage() {

        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG,"File location $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }

    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }





    }
}

class User(val uid: String, val username: String, val profileImageUrl: String)