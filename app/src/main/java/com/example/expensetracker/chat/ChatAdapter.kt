package com.example.expensetracker.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val messageTv: TextView =
            view.findViewById(R.id.messageTv)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) 1 else 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {

        val layout =
            if (viewType == 1)
                R.layout.item_user_message
            else
                R.layout.item_ai_message

        val view =
            LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)

        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {
        holder.messageTv.text =
            messages[position].message
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}