package com.example.wesplitapplicartionapk

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReceivableViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.receivable_view)

        val receivableTable = findViewById<TableLayout>(R.id.receivableTable)

        // Load records from SharedPreferences
        val sharedPreferences = getSharedPreferences("WeSplit", MODE_PRIVATE)
        val gson = Gson()
        val recordsJson = sharedPreferences.getString("records", null)
        val type = object : TypeToken<MutableList<Record>>() {}.type
        val records: MutableList<Record> = gson.fromJson(recordsJson, type) ?: mutableListOf()

        // Populate the table with records
        for (record in records) {
            for (person in record.peopleList) {
                val row = TableRow(this)
                val expenseName = TextView(this).apply {
                    text = record.expenseName
                    setPadding(8, 8, 8, 8)
                }
                val personName = TextView(this).apply {
                    text = person
                    setPadding(8, 8, 8, 8)
                }
                val amountOwed = TextView(this).apply {
                    text = "${record.splitAmount}"
                    setPadding(8, 8, 8, 8)
                }
                row.addView(expenseName)
                row.addView(personName)
                row.addView(amountOwed)
                receivableTable.addView(row)
            }
        }
    }

    data class Record(
        val expenseName: String,
        val personName: String,
        val totalExpense: Double,
        val splitAmount: Double,
        val peopleList: List<String>
    )
}
