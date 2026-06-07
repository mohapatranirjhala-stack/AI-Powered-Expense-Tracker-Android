package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.MainActivity
import com.example.expensetracker.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val emailEt = findViewById<EditText>(R.id.regEmail)
        val passwordEt = findViewById<EditText>(R.id.regPassword)
        val createBtn = findViewById<Button>(R.id.createBtn)

        createBtn.setOnClickListener {

            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter all fields",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            Toast.makeText(
                                this,
                                "Registration Successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(this, MainActivity::class.java)
                            )

                            finish()

                        } else {

                            Toast.makeText(
                                this,
                                task.exception?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}