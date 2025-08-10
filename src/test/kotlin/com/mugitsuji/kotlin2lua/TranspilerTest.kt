package com.mugitsuji.kotlin2lua

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TranspilerTest {
    
    private val transpiler = Kotlin2LuaTranspiler()
    
    @Test
    fun `test simple variable declaration`() {
        val kotlinCode = "val x = 42"
        val luaCode = transpiler.transpile(kotlinCode)
        assertEquals("local x = 42", luaCode.trim())
    }
    
    @Test
    fun `test string variable declaration`() {
        val kotlinCode = """val message = "Hello, World!""""
        val luaCode = transpiler.transpile(kotlinCode)
        assertEquals("""local message = "Hello, World!"""", luaCode.trim())
    }
    
    @Test
    fun `test simple function declaration`() {
        val kotlinCode = """
            fun greet(name: String) {
                return "Hello, " + name
            }
        """.trimIndent()
        
        val luaCode = transpiler.transpile(kotlinCode)
        val expected = """
            function greet(name)
                return "Hello, " + name
            end
        """.trimIndent()
        
        assertEquals(expected, luaCode.trim())
    }
    
    @Test
    fun `test function with multiple parameters`() {
        val kotlinCode = """
            fun add(a: Int, b: Int): Int {
                return a + b
            }
        """.trimIndent()
        
        val luaCode = transpiler.transpile(kotlinCode)
        val expected = """
            function add(a, b)
                return a + b
            end
        """.trimIndent()
        
        assertEquals(expected, luaCode.trim())
    }
    
    @Test
    fun `test if statement`() {
        val kotlinCode = """
            fun max(a: Int, b: Int): Int {
                if (a > b) {
                    return a
                } else {
                    return b
                }
            }
        """.trimIndent()
        
        val luaCode = transpiler.transpile(kotlinCode)
        val expected = """
            function max(a, b)
                if a > b then
                    return a
                else
                    return b
                end
            end
        """.trimIndent()
        
        assertEquals(expected, luaCode.trim())
    }
    
    @Test
    fun `test boolean literals`() {
        val kotlinCode = """
            val isTrue = true
            val isFalse = false
        """.trimIndent()
        
        val luaCode = transpiler.transpile(kotlinCode)
        val expected = """
            local isTrue = true
            local isFalse = false
        """.trimIndent()
        
        assertEquals(expected, luaCode.trim())
    }
    
    @Test
    fun `test function call`() {
        val kotlinCode = """
            fun main() {
                println("Hello, World!")
            }
        """.trimIndent()
        
        val luaCode = transpiler.transpile(kotlinCode)
        val expected = """
            function main()
                println("Hello, World!")
            end
        """.trimIndent()
        
        assertEquals(expected, luaCode.trim())
    }
}