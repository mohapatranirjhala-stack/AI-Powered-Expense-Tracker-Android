package com.example.expensetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_expense)

        db = FirebaseFirestore.getInstance()

        val titleEt =
            findViewById<EditText>(R.id.titleEt)

        val amountEt =
            findViewById<EditText>(R.id.amountEt)

        val saveBtn =
            findViewById<Button>(R.id.saveBtn)

        saveBtn.setOnClickListener {

            val title = titleEt.text.toString().trim()

            val amount = amountEt.text.toString().trim()

            if (title.isEmpty() || amount.isEmpty()) {

                Toast.makeText(
                    this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val currentDate =
                    SimpleDateFormat(
                        "dd MMM yy",
                        Locale.getDefault()
                    ).format(Date())
                val document =
                    db.collection("expenses")
                        .document()

                val expense = Expense(
                    document.id,
                    title,
                    amount,
                    currentDate
                )

                document.set(expense)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Expense Saved",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
            }
        }
    }
}