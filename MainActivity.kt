package com.example.wesplit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var totalExpenseInput: EditText
    private lateinit var personNameInput: EditText
    private lateinit var expenseNameInput: EditText
    private lateinit var peopleNamesContainer: LinearLayout
    private lateinit var addPersonButton: Button
    private lateinit var weSplitButton: Button
    private lateinit var resultTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        totalExpenseInput = findViewById(R.id.totalExpenseInput)
        personNameInput = findViewById(R.id.personNameInput)
        expenseNameInput = findViewById(R.id.expenseNameInput)
        peopleNamesContainer = findViewById(R.id.peopleNamesContainer)
        addPersonButton = findViewById(R.id.addPersonButton)
        weSplitButton = findViewById(R.id.weSplitButton)
        resultTextView = findViewById(R.id.resultTextView)

        // Set button click listeners
        addPersonButton.setOnClickListener {
            addPersonInput()
        }

        weSplitButton.setOnClickListener {
            splitExpense()
        }

        val receivablesButton = findViewById<Button>(R.id.receivablesButton)
        receivablesButton.setOnClickListener {
            val intent = Intent(this, ReceivableViewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addPersonInput() {
        val personNameInput = EditText(this).apply {
            hint = "Person Name"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }
        peopleNamesContainer.addView(personNameInput)
    }

    private fun splitExpense() {
        val totalExpenseStr = totalExpenseInput.text.toString()
        val personName = personNameInput.text.toString()
        val expenseName = expenseNameInput.text.toString()

        if (totalExpenseStr.isNotEmpty() && personName.isNotEmpty() && expenseName.isNotEmpty()) {
            val totalExpense = totalExpenseStr.toDouble()
            val peopleCount = peopleNamesContainer.childCount
            val splitAmount = totalExpense / (peopleCount)

            val result = StringBuilder()
            result.append("Expense Name: $expenseName\n")
            result.append("Total Expense: $totalExpense\n")
            result.append("Paid by: $personName\n")
            result.append("Each person owes: $splitAmount\n")
            result.append("Total People: ${peopleCount}\n")

            val peopleList = mutableListOf<String>()
            for (i in 0 until peopleCount) {
                val personEditText = peopleNamesContainer.getChildAt(i) as EditText
                val person = personEditText.text.toString()
                if (person.isNotEmpty()) {
                    result.append("$person owes $personName: $splitAmount\n")
                    peopleList.add(person)
                }
            }

            resultTextView.text = result.toString()

            saveRecord(Record(expenseName, personName, totalExpense, splitAmount, peopleList))
        } else {
            resultTextView.text = "Please fill in all fields."
        }
    }

    private fun saveRecord(record: Record) {
        val sharedPreferences = getSharedPreferences("WeSplit", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val existingRecordsJson = sharedPreferences.getString("records", null)
        val type = object : TypeToken<MutableList<Record>>() {}.type
        val records: MutableList<Record> = gson.fromJson(existingRecordsJson, type) ?: mutableListOf()

        records.add(record)
        val recordsJson = gson.toJson(records)
        editor.putString("records", recordsJson)
        editor.apply()
    }

    data class Record(
        val expenseName: String,
        val personName: String,
        val totalExpense: Double,
        val splitAmount: Double,
        val peopleList: List<String>
    )
}
