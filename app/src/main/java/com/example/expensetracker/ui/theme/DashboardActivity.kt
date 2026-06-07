package com.example.expensetracker

import android.content.Intent
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.appcompat.app.AlertDialog
import android.widget.EditText


import android.os.Bundle
import android.widget.Button

import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.text.Editable
import android.text.TextWatcher
import androidx.cardview.widget.CardView
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import java.io.File
import java.io.FileOutputStream
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatDelegate

import android.graphics.Color
import android.view.View
import android.widget.ImageView

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var nameTv: TextView
    private lateinit var expenseList: ArrayList<Expense>
    private lateinit var adapter: ExpenseAdapter
    private lateinit var searchEt: EditText

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var totalExpenseTv: TextView
    private lateinit var pieChart: PieChart
    private lateinit var highestExpenseTv: TextView
    private lateinit var transactionCountTv: TextView
    private lateinit var averageExpenseTv: TextView
    private lateinit var topCategoryTv: TextView
    private lateinit var weekBtn: Button
    private lateinit var monthBtn: Button

    private var currentFilter = "MONTH"
    private var filteredExpenses =
        ArrayList<Expense>()
    private lateinit var lineChart: LineChart

    private lateinit var welcomeTv: TextView
    private lateinit var budgetTv: TextView
    private lateinit var remainingTv: TextView
    private lateinit var budgetProgress: ProgressBar
    private lateinit var progressPercentTv: TextView

    private lateinit var budgetAlertTv: TextView


    private var totalExpense = 0

    private var monthlyBudget = 20000
    private var pdfFile: File? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.recyclerView)
        totalExpenseTv = findViewById(R.id.totalExpenseTv)
        pieChart = findViewById(R.id.pieChart)
        highestExpenseTv =
            findViewById(R.id.highestExpenseTv)

        transactionCountTv =
            findViewById(R.id.transactionCountTv)

        averageExpenseTv =
            findViewById(R.id.averageExpenseTv)

        topCategoryTv =
            findViewById(R.id.topCategoryTv)
        weekBtn =
            findViewById(R.id.weekBtn)

        monthBtn =
            findViewById(R.id.monthBtn)
        weekBtn.setOnClickListener {

            currentFilter = "WEEK"

            loadExpenses()
        }

        monthBtn.setOnClickListener {

            currentFilter = "MONTH"

            loadExpenses()
        }
        lineChart = findViewById(R.id.lineChart)
        nameTv = findViewById(R.id.nameTv)

        searchEt = findViewById(R.id.searchEt)

        welcomeTv = findViewById(R.id.welcomeTv)
        budgetTv = findViewById(R.id.budgetTv)
        val prefs =
            getSharedPreferences(
                "expense_tracker",
                MODE_PRIVATE
            )

        monthlyBudget =
            prefs.getInt(
                "monthly_budget",
                20000
            )

        remainingTv = findViewById(R.id.remainingTv)
        budgetProgress = findViewById(R.id.budgetProgress)
        progressPercentTv = findViewById(R.id.progressPercentTv)
        val budgetCard =
            findViewById<CardView>(
                R.id.budgetCard
            )
        budgetCard.setOnClickListener {

            val editText = EditText(this)

            editText.setText(
                monthlyBudget.toString()
            )

            AlertDialog.Builder(this)
                .setTitle("Update Monthly Budget")
                .setView(editText)

                .setPositiveButton(
                    "Save"
                ) { _, _ ->

                    monthlyBudget =
                        editText.text.toString()
                            .toIntOrNull()
                            ?: monthlyBudget
                    budgetTv.text =
                        "₹$monthlyBudget"

                    prefs.edit()
                        .putInt(
                            "monthly_budget",
                            monthlyBudget
                        )
                        .apply()

                    calculateTotal()
                }

                .setNegativeButton(
                    "Cancel",
                    null
                )
                .show()
        }






        budgetAlertTv =
            findViewById(R.id.budgetAlertTv)
        val pdfCard =
            findViewById<CardView>(
                R.id.pdfCard
            )

        pdfCard.setOnClickListener {

            createPdf()
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.inflateMenu(R.menu.dashboard_menu)

        toolbar.setOnMenuItemClickListener { item ->

            when(item.itemId){

                R.id.addExpense -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                    true
                }

                R.id.darkMode -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                    true
                }

                R.id.lightMode -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                    true
                }

                R.id.logout -> {
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                    true
                }

                else -> false
            }
        }







        val email =
            auth.currentUser?.email ?: ""

        val hour =
            java.util.Calendar.getInstance()
                .get(java.util.Calendar.HOUR_OF_DAY)

        val greeting =
            when {
                hour < 12 -> "Good Morning,"
                hour < 17 -> "Good Afternoon,"
                else -> "Good Evening,"
            }

        welcomeTv.text = greeting

        val name =
            email.substringBefore(".")
                .replaceFirstChar {
                    it.uppercase()
                }

        nameTv.text = name

        budgetTv.text =
            "₹$monthlyBudget"


        expenseList = ArrayList()

        adapter =
            ExpenseAdapter(expenseList) { expense ->

                Toast.makeText(
                    this,
                    "Deleting: ${expense.id}",
                    Toast.LENGTH_SHORT
                ).show()

                db.collection("expenses")
                    .document(expense.id)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Deleted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadExpenses()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Delete Failed: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            adapter

        searchEt.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                filterExpenses(
                    s.toString()
                )
            }

            override fun afterTextChanged(
                s: Editable?
            ) {
            }
        })



        loadExpenses()




    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {

        db.collection("expenses")
            .get()
            .addOnSuccessListener { result ->

                Toast.makeText(
                    this,
                    "Documents Found: ${result.size()}",
                    Toast.LENGTH_LONG
                ).show()

                expenseList.clear()

                for (document in result) {

                    val expense =
                        document.toObject(
                            Expense::class.java
                        )

                    expense.id = document.id

                    expenseList.add(expense)
                }


                applyFilter()

                adapter =
                    ExpenseAdapter(
                        filteredExpenses
                    ) { expense ->

                    }

                recyclerView.adapter =
                    adapter

                calculateTotal()

                updateAnalytics()

                setupPieChart()

                setupLineChart()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Firestore Error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun applyFilter() {

        filteredExpenses.clear()

        val inputFormat =
            java.text.SimpleDateFormat(
                "dd MMM yy",
                java.util.Locale.getDefault()
            )

        val today =
            java.util.Calendar.getInstance()

        for(expense in expenseList){

            val expenseDate =
                inputFormat.parse(
                    expense.date
                ) ?: continue

            val diff =
                today.time.time -
                        expenseDate.time

            val days =
                diff / (1000 * 60 * 60 * 24)

            if(currentFilter == "WEEK"){

                if(days <= 7){

                    filteredExpenses.add(
                        expense
                    )
                }

            }else{

                filteredExpenses.add(
                    expense
                )
            }
        }
    }


    private fun filterExpenses(
        query: String
    ) {

        val filteredList =
            expenseList.filter {

                it.title.contains(
                    query,
                    ignoreCase = true
                )
            }

        adapter =
            ExpenseAdapter(
                ArrayList(filteredList)
            ) { expense ->

            }

        recyclerView.adapter =
            adapter
    }

    private fun calculateTotal() {

        var total = 0

        for (expense in filteredExpenses) {

            total +=
                expense.amount.toIntOrNull()
                    ?: 0
        }
        totalExpense = total

        totalExpenseTv.text =
            "₹$total"

        val remaining =
            monthlyBudget - total

        remainingTv.text =
            "₹$remaining"

        val percentage =
            ((total.toFloat() /
                    monthlyBudget) * 100)
                .toInt()
                .coerceAtMost(100)

        budgetProgress.progress =
            percentage


        when {

            percentage < 50 -> {

                budgetAlertTv.text =
                    "✅ You're well within budget"

                budgetAlertTv.setBackgroundColor(
                    Color.parseColor("#DCFCE7")
                )
            }

            percentage < 80 -> {

                budgetAlertTv.text =
                    "⚠ You've used $percentage% of your budget"

                budgetAlertTv.setBackgroundColor(
                    Color.parseColor("#FEF3C7")
                )
            }

            else -> {

                budgetAlertTv.text =
                    "🚨 Budget limit almost reached"

                budgetAlertTv.setBackgroundColor(
                    Color.parseColor("#FEE2E2")
                )
            }
        }
    }
    private fun updateAnalytics() {

        if(filteredExpenses.isEmpty())
            return

        val highest =
            filteredExpenses.maxOf {
                it.amount.toIntOrNull() ?: 0
            }

        val totalTransactions =
            filteredExpenses.size

        val totalAmount =
            filteredExpenses.sumOf {
                it.amount.toIntOrNull() ?: 0
            }

        val average =
            totalAmount / totalTransactions

        val categoryMap =
            HashMap<String, Int>()

        filteredExpenses.forEach {

            categoryMap[it.title] =
                categoryMap.getOrDefault(
                    it.title,
                    0
                ) + 1
        }

        val topCategory =
            categoryMap.maxByOrNull {
                it.value
            }?.key ?: "-"

        highestExpenseTv.text =
            "Highest Expense: ₹$highest"

        transactionCountTv.text =
            "Transactions: $totalTransactions"

        averageExpenseTv.text =
            "Average Spend: ₹$average"

        topCategoryTv.text =
            "Top Category: $topCategory"
    }

    private fun setupPieChart() {

        val total =
            filteredExpenses.sumOf {
                it.amount.toIntOrNull() ?: 0
            }

        pieChart.centerText = "₹$total"

        val entries = ArrayList<PieEntry>()

        val categoryMap = HashMap<String, Float>()

        for (expense in filteredExpenses) {
            val amount =
                expense.amount.toFloatOrNull()
                    ?: 0f

            val category =
                expense.title.trim()
                    .lowercase()
                    .replaceFirstChar {
                        it.uppercase()
                    }

            categoryMap[category] =
                categoryMap.getOrDefault(
                    category,
                    0f
                ) + amount
        }

        for ((category, total) in categoryMap) {

            entries.add(
                PieEntry(
                    total,
                    category
                )
            )
        }

        if (entries.isEmpty()) {

            pieChart.clear()




            pieChart.invalidate()

            return
        }

        val dataSet =
            PieDataSet(entries, "")

        dataSet.colors = listOf(
            Color.parseColor("#2563EB"), // Blue
            Color.parseColor("#10B981"), // Green
            Color.parseColor("#F59E0B"), // Amber
            Color.parseColor("#EF4444"), // Red
            Color.parseColor("#8B5CF6"), // Purple
            Color.parseColor("#06B6D4"), // Cyan
            Color.parseColor("#EC4899"), // Pink
            Color.parseColor("#84CC16")  // Lime
        )

        dataSet.valueTextSize = 10f
        dataSet.sliceSpace = 3f

        val data =
            PieData(dataSet)
        data.setDrawValues(false)

        pieChart.data = data
        pieChart.setDrawEntryLabels(false)

        pieChart.centerText =
            "Spent\n₹$totalExpense"
        pieChart.setCenterTextSize(22f)
        pieChart.setCenterTextColor(
            Color.parseColor("#1E293B")
        )





        pieChart.setUsePercentValues(false)
        dataSet.selectionShift = 0f


        pieChart.setHoleColor(Color.WHITE)
        pieChart.isRotationEnabled = true


        pieChart.holeRadius = 55f

        pieChart.transparentCircleRadius =
            60f
        pieChart.setEntryLabelTextSize(10f)

        pieChart.setEntryLabelColor(
            Color.BLACK
        )

        pieChart.description.isEnabled =
            false

        pieChart.animateY(1400)

        pieChart.legend.isEnabled = true

        pieChart.legend.textSize = 14f
        pieChart.legend.formSize = 14f

        pieChart.legend.xEntrySpace = 12f

        pieChart.legend.yEntrySpace = 8f

        pieChart.legend.isWordWrapEnabled = true

        pieChart.invalidate()
    }
    private fun setupLineChart() {

        val entries = ArrayList<Entry>()

        val inputFormat =
            SimpleDateFormat(
                "dd MMM yy",
                Locale.getDefault()
            )

        val groupedExpenses =
            filteredExpenses.groupBy {

                inputFormat.parse(it.date)
            }

        val dateLabels =
            HashMap<Float, String>()

        var index = 0f

        groupedExpenses
            .toSortedMap()
            .forEach { (date, expenses) ->

                val total =
                    expenses.sumOf {
                        it.amount.toIntOrNull() ?: 0
                    }

                entries.add(
                    Entry(
                        index,
                        total.toFloat()
                    )
                )

                dateLabels[index] =
                    SimpleDateFormat(
                        "dd MMM",
                        Locale.getDefault()
                    ).format(date)

                index++
            }
        if(entries.size < 2){

            lineChart.clear()

            lineChart.setNoDataText(
                "Add expenses on multiple days to see trends"
            )

            lineChart.invalidate()

            return
        }


        val dataSet =
            LineDataSet(entries, "Monthly Spending")

        dataSet.mode =
            LineDataSet.Mode.CUBIC_BEZIER

        dataSet.color =
            Color.parseColor("#2563EB")

        dataSet.lineWidth = 3f

        dataSet.circleRadius = 5f

        dataSet.setCircleColor(
            Color.parseColor("#2563EB")
        )

        dataSet.valueTextSize = 10f
        dataSet.setDrawValues(false)

        dataSet.setDrawFilled(true)

        dataSet.fillAlpha = 50
        dataSet.fillColor =
            Color.parseColor("#2563EB")





        val lineData = LineData(dataSet)

        lineChart.data = lineData
        lineChart.xAxis.valueFormatter =
            object : ValueFormatter() {

                override fun getFormattedValue(
                    value: Float
                ): String {

                    return dateLabels[value]
                        ?: ""
                }
            }

        lineChart.xAxis.granularity = 1f

        lineChart.xAxis.position =
            com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM



        lineChart.axisLeft.granularity = 500f
        lineChart.axisRight.isEnabled = false

        lineChart.xAxis.setDrawGridLines(false)

        lineChart.axisLeft.setDrawGridLines(false)

        lineChart.legend.isEnabled = false

        lineChart.setTouchEnabled(true)

        lineChart.setPinchZoom(false)

        lineChart.setScaleEnabled(false)









        lineChart.description.isEnabled = false

        lineChart.animateX(1200)

        lineChart.invalidate()
    }

    private fun createPdf() {

        try {

            val pdfDocument = PdfDocument()
            val paint = Paint()

            val pageInfo =
                PdfDocument.PageInfo.Builder(
                    595,
                    842,
                    1
                ).create()

            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas

            var y = 60

            paint.textSize = 24f

            canvas.drawText(
                "Expense Tracker Report",
                50f,
                y.toFloat(),
                paint
            )

            y += 40

            paint.textSize = 18f

            canvas.drawText(
                "Total Expense: ₹$totalExpense",
                50f,
                y.toFloat(),
                paint
            )

            y += 50

            paint.textSize = 16f

            for (expense in expenseList) {

                canvas.drawText(
                    "${expense.title} - ₹${expense.amount}",
                    50f,
                    y.toFloat(),
                    paint
                )

                y += 30
            }

            pdfDocument.finishPage(page)

            pdfFile =
                File(
                    getExternalFilesDir(null),
                    "Expense_Report.pdf"
                )

            pdfDocument.writeTo(
                FileOutputStream(pdfFile)
            )

            pdfDocument.close()

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("PDF Generated")
                .setMessage("Expense report saved successfully")
                .setPositiveButton("Open") { _, _ ->
                    openPdf()
                }
                .setNegativeButton("Share") { _, _ ->
                    sharePdf()
                }
                .show()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openPdf() {

        if (pdfFile == null || !pdfFile!!.exists()) {

            Toast.makeText(
                this,
                "PDF not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val uri =
            androidx.core.content.FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                pdfFile!!
            )

        val intent = Intent(Intent.ACTION_VIEW)

        intent.setDataAndType(
            uri,
            "application/pdf"
        )

        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        startActivity(
            Intent.createChooser(
                intent,
                "Open PDF"
            )
        )
    }

    private fun sharePdf() {

        if (pdfFile == null || !pdfFile!!.exists()) {
            Toast.makeText(this, "PDF not found", Toast.LENGTH_SHORT).show()
            return
        }

        val uri =
            androidx.core.content.FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                pdfFile!!
            )

        val shareIntent = Intent(Intent.ACTION_SEND)

        shareIntent.type = "application/pdf"
        shareIntent.putExtra(
            Intent.EXTRA_STREAM,
            uri
        )

        shareIntent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        startActivity(
            Intent.createChooser(
                shareIntent,
                "Share PDF"
            )
        )
    }

}