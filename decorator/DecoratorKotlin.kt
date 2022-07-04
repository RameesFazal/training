package com.example.kotlintraining.designpattern.decorator

import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

interface DataSource {
    fun writeData(data: String)
    fun readData(): String
}

open class DataSourceDecorator(private val dataSource: DataSource) : DataSource {
    override fun writeData(data: String) {
        dataSource.writeData(data)
    }

    override fun readData(): String {
        return dataSource.readData()
    }
}

internal class FileDataSource(private val name: String) : DataSource {
    override fun writeData(data: String) {
        val file = File(name)
        try {
            FileOutputStream(file).write(
                data.toByteArray(),
                0,
                data.length
            )
        } catch (ex: IOException) {
            println(ex.message)
        }
    }

    override fun readData(): String {
        var buffer: CharArray? = null
        val file = File(name)
        try {
            FileReader(file).let { reader ->
                buffer = CharArray(file.length().toInt())
                reader.read(buffer)
            }
        } catch (ex: IOException) {
            println(ex.message)
        }
        return String(buffer!!)
    }
}

class CompressionDecorator(source: DataSource) : DataSourceDecorator(source) {

    override fun writeData(data: String) {
        super.writeData(compress(data))
    }

    override fun readData(): String {
        return decompress(super.readData())
    }

    private fun compress(stringData: String): String {
        return "Compressed String"
    }

    private fun decompress(stringData: String): String {
        return "Decompressed String"
    }
}

fun main() {
    val salaryRecords = "Name,Salary\nRamees,100\nFazal,200"
    val encoded: DataSourceDecorator = CompressionDecorator(
        FileDataSource("Output/OutputDemo.txt")
    )
    encoded.writeData(salaryRecords)
    val plain: DataSource = FileDataSource("Output/OutputDemo.txt")

    println("Input")
    println(salaryRecords)
    println("Encoded")
    println(plain.readData())
    println("Decoded")
    println(encoded.readData())
}