package com.cmpt362team21.ui.util

import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// dummy class to create a csv file in the emulator
class CreateCSVFile {
    fun initializeFinancialData() {
        val data = "Expenses,Utilities,Gas,120,2023-11-10\n" +
                "Expenses,Car,Insurance,130,2023-11-12\n" +
                "Expenses,House,Mortgage,1800,2023-11-10\n" +
                "Incomes,Stocks,Options,300,2023-11-10\n" +
                "Incomes,House,Rent,1200,2023-11-10\n" +
                "Expenses,Food,Walmart,1445,2023-11-10\n" +
                "Expenses,Utilities,Electricity,100,2023-11-10\n" +
                "Expenses,Personal,Phone,140,2023-11-10\n" +
                "Expenses,Car,Payment,390,2023-11-10\n" +
                "Incomes,Stocks,Salary,4000,2023-11-15\n" +
                "Incomes,Side Job,Freelancing,800,2023-11-20\n" +
                "Expenses,Entertainment,Movie Night,50,2023-11-22\n" +
                "Expenses,Health,Doctor's Visit,200,2023-11-25\n" +
                "Incomes,Investments,Dividends,150,2023-11-28\n" +
                "Expenses,Food,Restaurant,75,2023-11-30\n" +
                "Expenses,Utilities,Water Bill,40,2023-11-05\n" +
                "Expenses,Car,Registration Fee,200,2023-11-08\n" +
                "Expenses,House,Property Tax,300,2023-11-15\n" +
                "Expenses,Food,Groceries,250,2023-11-18\n" +
                "Expenses,Utilities,Internet,80,2023-11-20\n" +
                "Expenses,Health,Prescription,30,2023-11-25\n" +
                "Expenses,Car,Maintenance,150,2023-11-28\n" +
                "Expenses,Entertainment,Concert Tickets,120,2023-11-30\n" +
                "Incomes,Career,Dentist,3500,2023-12-10\n" +
                "Incomes,Job,Salary,4500,2023-12-05\n" +
                "Expenses,Shopping,Christmas Gifts,300,2023-12-10\n" +
                "Expenses,Utilities,Heating,120,2023-12-15\n" +
                "Incomes,Investments,Stock Dividends,200,2023-12-20\n" +
                "Expenses,Entertainment,Holiday Party,100,2023-12-22\n" +
                "Expenses,Health,Gym Membership,50,2023-12-28\n" +
                "Expenses,Food,New Year's Eve Dinner,80,2023-12-31\n" +
                "Incomes,Stocks,Year-End Bonus,800,2023-12-20\n" +
                "Expenses,Car,Maintenance,120,2023-12-18\n" +
                "Expenses,Health,Flu Medicine,20,2023-10-05\n" +
                "Expenses,Utilities,Internet,80,2023-10-10\n" +
                "Incomes,Job,Salary,4000,2023-10-15\n" +
                "Expenses,Entertainment,Movie Night,40,2023-10-20\n" +
                "Incomes,Investments,Dividends,150,2023-10-25\n" +
                "Expenses,Food,Groceries,200,2023-10-28\n" +
                "Expenses,Utilities,Electricity,90,2023-10-31\n" +
                "Incomes,Side Job,Freelancing,600,2023-10-12\n" +
                "Expenses,Car,Registration Fee,150,2023-10-22\n" +
                "Expenses,Car,Maintenance,100,2023-09-05\n" +
                "Incomes,Side Job,Freelancing,600,2023-09-10\n" +
                "Expenses,Utilities,Water Bill,30,2023-09-15\n" +
                "Incomes,Investments,Stock Dividends,180,2023-09-20\n" +
                "Expenses,Health,Doctor's Visit,120,2023-09-25\n" +
                "Expenses,Entertainment,Concert Tickets,75,2023-09-28\n" +
                "Expenses,Food,Restaurant,50,2023-09-30\n" +
                "Incomes,Job,Salary,3800,2023-09-15\n" +
                "Expenses,Utilities,Internet,70,2023-09-18\n" +
                "Expenses,Car,Insurance,120,2023-08-05\n" +
                "Incomes,Job,Salary,3500,2023-08-10\n" +
                "Expenses,House,Rent,1000,2023-08-15\n" +
                "Expenses,Utilities,Electricity,80,2023-08-20\n" +
                "Incomes,Investments,Dividends,120,2023-08-25\n" +
                "Expenses,Food,Walmart,100,2023-08-28\n" +
                "Expenses,Health,Gym Membership,50,2023-08-31\n" +
                "Incomes,Side Job,Freelancing,600,2023-08-12\n" +
                "Expenses,Entertainment,Movie Night,40,2023-08-22\n"


        // inspired by: https://www.baeldung.com/kotlin/csv-files
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(folder, "finances.csv")

        if (!folder.exists()) {
            folder.mkdirs()
        }

        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var stream: FileOutputStream? = null
        try {
            stream = FileOutputStream(file, true)
            stream.write(data.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                stream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}