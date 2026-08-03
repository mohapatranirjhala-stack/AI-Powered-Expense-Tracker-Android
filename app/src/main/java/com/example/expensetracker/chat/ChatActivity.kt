package com.example.expensetracker.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.network.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.expensetracker.Expense
import com.google.firebase.firestore.FirebaseFirestore
import com.example.expensetracker.ai.FinancialPromptBuilder

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEt: EditText
    private lateinit var sendBtn: Button

    private lateinit var adapter: ChatAdapter

    private val messages =
        mutableListOf<ChatMessage>()

    private val expenseList =
        mutableListOf<Expense>()

    private lateinit var db:
            FirebaseFirestore

    private var monthlyBudget = 20000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        db = FirebaseFirestore.getInstance()
        val prefs = getSharedPreferences(
            "expense_tracker",
            MODE_PRIVATE
        )

        monthlyBudget =
            prefs.getInt(
                "monthly_budget",
                20000
            )

        recyclerView = findViewById(R.id.chatRecyclerView)
        messageEt = findViewById(R.id.messageEt)
        sendBtn = findViewById(R.id.sendBtn)

        adapter = ChatAdapter(messages)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        loadExpenses()
        addMessage(
            "👋 Hi! I'm FinWise AI.\n\nAsk me anything about your expenses, budget, or savings.",
            false
        )

        sendBtn.setOnClickListener {

            val prompt = messageEt.text.toString().trim()

            if (prompt.isEmpty()) {
                return@setOnClickListener
            }

            addMessage(prompt, true)
            messageEt.setText("")

            lifecycleScope.launch {

                addMessage("🤖 FinWise AI is analyzing your expenses...", false)

                val financialPrompt =
                    FinancialPromptBuilder.buildPrompt(
                        userQuestion = prompt,
                        expenses = expenseList,
                        monthlyBudget = monthlyBudget
                    )

                val reply =
                    try {

                        withContext(Dispatchers.IO) {

                            GeminiService.askGemini(
                                financialPrompt
                            )
                        }

                    } catch (e: Exception) {

                        "❌ Unable to contact FinWise AI.\n\n${e.localizedMessage}"
                    }

                messages.removeAt(messages.lastIndex)

                adapter.notifyItemRemoved(
                    messages.size
                )

                addMessage(reply, false)
            }
        }
    }
    private fun loadExpenses() {

        db.collection("expenses")
            .get()
            .addOnSuccessListener { result ->

                expenseList.clear()

                for (document in result) {

                    val expense =
                        document.toObject(
                            Expense::class.java
                        )

                    expense.id =
                        document.id

                    expenseList.add(expense)
                }

            }
    }

    private fun addMessage(
        text: String,
        isUser: Boolean
    ) {
        messages.add(ChatMessage(text, isUser))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }
}