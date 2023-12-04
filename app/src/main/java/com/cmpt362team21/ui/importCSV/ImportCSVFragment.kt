package com.cmpt362team21.ui.importCSV

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream

class ImportCSVFragment : Fragment() {
    private var _binding: FragmentImportCsvBinding? = null
    private var expensesChart: AnyChartView? = null
    private var incomesChart: AnyChartView? = null
    private var barChartView: AnyChartView? = null
    private val binding get() = _binding!!
    private lateinit var pickFileLauncher: ActivityResultLauncher<Intent>
    private val entries: MutableList<CSVFileEntry> = mutableListOf()
    private val entriesKey = "entriesKey"

    // pdf related code inspired by: https://www.geeksforgeeks.org/generate-pdf-file-in-android-using-kotlin/
    private fun exportPDF() {
        if (writeToExternalStorage()) {
            val pdfDocument = PdfDocument()

            val expensesChartPage = PdfDocument.PageInfo.Builder(1200, 700, 1).create()
            val pageExpenses = pdfDocument.startPage(expensesChartPage)
            val canvasExpenses: Canvas = pageExpenses.canvas
            plotGraphOnPDF(canvasExpenses, expensesChart, "Expenses")
            pdfDocument.finishPage(pageExpenses)

            val incomeChartPage = PdfDocument.PageInfo.Builder(1200, 700, 2).create()
            val pageIncomes = pdfDocument.startPage(incomeChartPage)
            val canvasIncomes: Canvas = pageIncomes.canvas
            plotGraphOnPDF(canvasIncomes, incomesChart, "Incomes")
            pdfDocument.finishPage(pageIncomes)

            val barChartPage = PdfDocument.PageInfo.Builder(1200, 700, 3).create()
            val pageBar = pdfDocument.startPage(barChartPage)
            val canvasBar: Canvas = pageBar.canvas
            plotGraphOnPDF(canvasBar, barChartView, "Expenses vs Incomes")
            pdfDocument.finishPage(pageBar)

            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            dir.mkdirs()

            val pdfFile = File(dir, "finances.pdf")
            try {
                pdfDocument.writeTo(FileOutputStream(pdfFile))
                Toast.makeText(requireContext(), "PDF was exported successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error while exporting PDF", Toast.LENGTH_SHORT).show()
            }

            pdfDocument.close()
            openPDF(pdfFile)
        } else {
            Toast.makeText(requireContext(), "External storage is not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun plotGraphOnPDF(canvas: Canvas, chartView: AnyChartView?, chartTitle: String) {
        APIlib.getInstance().setActiveAnyChartView(chartView)
        val bitmap = Bitmap.createBitmap(chartView!!.width, chartView.height, Bitmap.Config.ARGB_8888)
        val chartCanvas = Canvas(bitmap)
        chartView.draw(chartCanvas)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    private fun writeToExternalStorage(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun openPDF(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(requireContext(), "com.cmpt362team21.fileprovider", file)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Cannot view PDF", Toast.LENGTH_SHORT).show()
        }
    }

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
        setupExportButton()

        return binding.root
    }
    private fun setupExportButton() {
        _binding?.btnExportPDF?.setOnClickListener {
            exportPDF()
        }
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
