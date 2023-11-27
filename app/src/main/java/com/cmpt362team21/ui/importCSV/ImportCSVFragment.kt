package com.cmpt362team21.ui.importCSV

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Bar
import com.anychart.enums.Anchor
import com.cmpt362team21.databinding.FragmentImportCsvBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class ImportCSVFragment : Fragment() {
    private var _binding: FragmentImportCsvBinding? = null
    private var expensesChart: AnyChartView? = null
    private var incomesChart: AnyChartView? = null
    private var barChartView: AnyChartView? = null
    private val binding get() = _binding!!
    private lateinit var pickFileLauncher: ActivityResultLauncher<Intent>
    private val entries: MutableList<CSVFileEntry> = mutableListOf()
    private val entriesKey = "entriesKey"

    private fun List<CSVFileEntry>.toJson(): String {
        return Gson().toJson(this)
    }

    private fun String.toEntriesList(): List<CSVFileEntry> {
        val type = object : TypeToken<List<CSVFileEntry>>() {}.type
        return Gson().fromJson(this, type)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val entriesJson = getEntriesFromSharedPreferences()

        if (entriesJson.isNotEmpty()) {
            entries.clear()
            entries.addAll(entriesJson.toEntriesList())

            val expenses = entries.filter { it.transactionType.equals("Expenses", ignoreCase = true) }
            val incomes = entries.filter { it.transactionType.equals("Incomes", ignoreCase = true) }

            createPieChart(expensesChart, "Expenses", expenses)
            createPieChart(incomesChart, "Incomes", incomes)

            val monthlyData = entries.groupBy { it.dateTransaction.substring(0, 7) }
            createBarChart(barChartView, "Expenses vs Incomes", monthlyData)
        }
    }

    private fun saveEntriesToSharedPreferences(entriesJson: String) {
        val sharedPreferences = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(entriesKey, entriesJson)
        editor.apply()
    }

    private fun getEntriesFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        return sharedPreferences.getString(entriesKey, "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CSV", "onCreateView")

        _binding = FragmentImportCsvBinding.inflate(inflater, container, false)
        expensesChart = _binding!!.chartExpenses
        incomesChart = _binding!!.incomesGraph
        barChartView = _binding!!.barChart

        setupFilePicker()
        setupImportButton()

        return binding.root
    }

    private fun setupFilePicker() {
        pickFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val selectedFileUri: Uri? = data?.data

                    if (selectedFileUri != null) {
                        processSelectedFile(selectedFileUri)
                    }
                }
            }
    }

    private fun processSelectedFile(selectedFileUri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(selectedFileUri)
            inputStream?.bufferedReader()?.useLines { lines ->
                entries.clear()
                lines.forEach { line ->
                    parseCSVLine(line)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CSV", "Error reading CSV file: ${e.message}")
        }

        val entriesJson = entries.toJson()
        saveEntriesToSharedPreferences(entriesJson)

        val expenses = entries.filter { it.transactionType.equals("Expenses", ignoreCase = true) }
        val incomes = entries.filter { it.transactionType.equals("Incomes", ignoreCase = true) }

        createPieChart(expensesChart, "Expenses", expenses)
        createPieChart(incomesChart, "Incomes", incomes)

        val monthlyData = entries.groupBy { it.dateTransaction.substring(0, 7) }
        createBarChart(barChartView, "Expenses vs Incomes", monthlyData)
    }

    private fun parseCSVLine(line: String) {
        val values = line.split(",")
        if (values.size == 5) {
            val entry = CSVFileEntry(values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim())
            entries.add(entry)
        } else {
            Log.e("CSV", "Invalid CSV line: $line")
        }
    }

    private fun setupImportButton() {
        _binding!!.btnImportCSV.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(
                DocumentsContract.EXTRA_INITIAL_URI,
                Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADownload")
            )
            pickFileLauncher.launch(intent)
        }
    }

    // inspired by: https://github.com/AnyChart/AnyChart-Android/blob/master/sample/src/main/java/com/anychart/sample/charts/PieChartActivity.java
    private fun createPieChart(chartView: AnyChartView?, chartTitle: String, data: List<CSVFileEntry>) {
        APIlib.getInstance().setActiveAnyChartView(chartView)
        val pie = AnyChart.pie()
        val graphData = mutableMapOf<String, Double>()

        data.forEach { entry ->
            val categoryType = entry.categoryType
            val amount = entry.amountTransaction.toDouble()

            if (graphData.containsKey(categoryType)) {
                graphData[categoryType] = graphData[categoryType]!! + amount
            } else {
                graphData[categoryType] = amount
            }
        }

        val filteredData = graphData.map { (categoryType, amount) ->
            ValueDataEntry(categoryType, amount)
        }

        pie.data(filteredData)
        pie.title(chartTitle)

        chartView?.let {
            it.isVisible = true
            it.setChart(pie)
        }
    }

    // inspired by: https://github.com/AnyChart/AnyChart-Android/blob/master/sample/src/main/java/com/anychart/sample/charts/BarChartActivity.java
    private fun createBarChart(chartView: AnyChartView?, chartTitle: String, data: Map<String, List<CSVFileEntry>>) {
        APIlib.getInstance().setActiveAnyChartView(chartView)
        val barChart = AnyChart.bar()

        val seriesData = data.flatMap { (month, entries) ->
            val totalExpenses = entries
                .filter { it.transactionType.equals("Expenses", ignoreCase = true) }
                .sumOf { it.amountTransaction.toDouble().toInt() }

            val totalIncomes = entries
                .filter { it.transactionType.equals("Incomes", ignoreCase = true) }
                .sumOf { it.amountTransaction.toDouble().toInt() }

            listOf(
                CustomDataEntry(month, "Expenses", totalExpenses),
                CustomDataEntry(month, "Incomes", totalIncomes)
            )
        }

        val series1: Bar = barChart.bar(seriesData.filter { it.category == "Expenses" })
        series1.name("Expenses")
        series1.color("red")
        series1.tooltip().position("right").anchor(Anchor.LEFT_CENTER)

        val series2: Bar = barChart.bar(seriesData.filter { it.category == "Incomes" })
        series2.name("Incomes")
        series2.color("green")
        series2.tooltip().position("left").anchor(Anchor.RIGHT_CENTER)

        barChart.legend().enabled(true)
        barChart.legend().inverted(true)
        barChart.legend().fontSize(13.0)
        barChart.legend().padding(0.0, 0.0, 20.0, 0.0)

        chartView!!.setChart(barChart)
        barChart.title(chartTitle)
        chartView.isVisible = true
    }

    private class CustomDataEntry(
        val x: String,
        val category: String,
        val value: Int
    ) : ValueDataEntry(x, value)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
