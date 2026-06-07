package com.example.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenseList: ArrayList<Expense>,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val icon: ImageView =
            itemView.findViewById(R.id.categoryIcon)




        val titleTv: TextView =
            itemView.findViewById(R.id.titleTv)

        val amountTv: TextView =
            itemView.findViewById(R.id.amountTv)

        val dateTv: TextView =
            itemView.findViewById(R.id.dateTv)

        val deleteBtn: Button =
            itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.expense_item,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val expense = expenseList[position]
        holder.titleTv.text = expense.title

        holder.amountTv.text =
            "₹ ${expense.amount}"

        holder.dateTv.text =
            expense.date
        when (expense.title.lowercase()) {

            "food", "restaurant" -> {
                holder.icon.setImageResource(
                    R.drawable.baseline_restaurant_24
                )
            }

            "travel", "flight", "flights" -> {
                holder.icon.setImageResource(
                    R.drawable.baseline_flight_24
                )
            }

            "shopping", "shop" -> {
                holder.icon.setImageResource(
                    R.drawable.baseline_shopping_bag_24
                )
            }

            "medical", "medicine", "health" -> {
                holder.icon.setImageResource(
                    R.drawable.baseline_medication_24
                )
            }

            "bills", "bill" -> {
                holder.icon.setImageResource(
                    R.drawable.baseline_receipt_24
                )
            }

            else -> {
                holder.icon.setImageResource(
                    R.drawable.baseline_receipt_24
                )
            }
        }




        holder.deleteBtn.setOnClickListener {

            onDeleteClick(expense)
        }
    }

    override fun getItemCount(): Int {

        return expenseList.size
    }
}