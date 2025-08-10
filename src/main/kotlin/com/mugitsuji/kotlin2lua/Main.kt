package com.mugitsuji.kotlin2lua

import java.io.File
import kotlin.system.exitProcess

/**
 * Main entry point for the Kotlin-to-Lua transpiler
 */
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printUsage()
        exitProcess(1)
    }
    
    val inputFile = args[0]
    val outputFile = if (args.size > 1) args[1] else inputFile.replace(".kt", ".lua")
    
    try {
        val file = File(inputFile)
        if (!file.exists()) {
            println("Error: Input file '$inputFile' does not exist")
            exitProcess(1)
        }
        
        val kotlinSource = file.readText()
        val transpiler = Kotlin2LuaTranspiler()
        val luaCode = transpiler.transpile(kotlinSource)
        
        File(outputFile).writeText(luaCode)
        
        println("Successfully transpiled '$inputFile' to '$outputFile'")
        
    } catch (e: TranspilerException) {
        println("Transpilation failed: ${e.message}")
        exitProcess(1)
    } catch (e: Exception) {
        println("Unexpected error: ${e.message}")
        exitProcess(1)
    }
}

private fun printUsage() {
    println("""
        Kotlin2Lua Transpiler
        
        Usage: kotlin2lua <input.kt> [output.lua]
        
        Arguments:
          input.kt   - Kotlin source file to transpile
          output.lua - Output Lua file (optional, defaults to input filename with .lua extension)
        
        Examples:
          kotlin2lua hello.kt
          kotlin2lua hello.kt hello_world.lua
    """.trimIndent())
}