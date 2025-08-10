package com.mugitsuji.kotlin2lua

import com.mugitsuji.kotlin2lua.parser.Lexer
import com.mugitsuji.kotlin2lua.parser.Parser
import com.mugitsuji.kotlin2lua.generator.LuaGenerator

/**
 * Main transpiler class that orchestrates the Kotlin-to-Lua compilation process
 */
class Kotlin2LuaTranspiler {
    
    /**
     * Transpiles Kotlin source code to Lua
     */
    fun transpile(kotlinSource: String): String {
        try {
            // Lexical analysis
            val lexer = Lexer(kotlinSource)
            val tokens = lexer.tokenize()
            
            // Parsing
            val parser = Parser(tokens)
            val ast = parser.parse()
            
            // Code generation
            val generator = LuaGenerator()
            return generator.generate(ast)
            
        } catch (e: Exception) {
            throw TranspilerException("Error transpiling Kotlin to Lua: ${e.message}", e)
        }
    }
}

/**
 * Exception thrown during transpilation
 */
class TranspilerException(message: String, cause: Throwable? = null) : Exception(message, cause)