package com.mugitsuji.kotlin2lua.generator

import com.mugitsuji.kotlin2lua.ast.*

/**
 * Generates Lua code from Kotlin AST
 */
class LuaGenerator {
    private val output = StringBuilder()
    private var indentLevel = 0
    
    fun generate(program: Program): String {
        output.clear()
        indentLevel = 0
        
        for ((index, declaration) in program.declarations.withIndex()) {
            generateDeclaration(declaration)
            if (index < program.declarations.size - 1) {
                output.appendLine()
            }
        }
        
        return output.toString().trim()
    }
    
    private fun generateDeclaration(declaration: Declaration) {
        when (declaration) {
            is FunctionDeclaration -> generateFunction(declaration)
            is VariableDeclaration -> generateVariable(declaration)
        }
    }
    
    private fun generateFunction(function: FunctionDeclaration) {
        // Lua function syntax: function name(params)
        output.append("function ${function.name}(")
        
        // Parameters (ignore types in Lua)
        function.parameters.forEachIndexed { index, param ->
            if (index > 0) output.append(", ")
            output.append(param.name)
        }
        
        output.append(")")
        output.appendLine()
        
        // Function body
        indentLevel++
        generateStatement(function.body)
        indentLevel--
        
        output.append("end")
    }
    
    private fun generateVariable(variable: VariableDeclaration) {
        // Lua local variable: local name = value
        output.append("local ${variable.name}")
        
        if (variable.initializer != null) {
            output.append(" = ")
            generateExpression(variable.initializer)
        }
    }
    
    private fun generateStatement(statement: Statement) {
        when (statement) {
            is Block -> generateBlock(statement)
            is ExpressionStatement -> {
                indent()
                generateExpression(statement.expression)
                output.appendLine()
            }
            is ReturnStatement -> {
                indent()
                output.append("return")
                if (statement.expression != null) {
                    output.append(" ")
                    generateExpression(statement.expression)
                }
                output.appendLine()
            }
            is IfStatement -> generateIf(statement)
            is VariableStatement -> {
                indent()
                generateVariable(statement.declaration)
                output.appendLine()
            }
        }
    }
    
    private fun generateBlock(block: Block) {
        for (statement in block.statements) {
            generateStatement(statement)
        }
    }
    
    private fun generateIf(ifStatement: IfStatement) {
        indent()
        output.append("if ")
        generateExpression(ifStatement.condition)
        output.appendLine(" then")
        
        indentLevel++
        generateStatement(ifStatement.thenStatement)
        indentLevel--
        
        if (ifStatement.elseStatement != null) {
            indent()
            output.appendLine("else")
            indentLevel++
            generateStatement(ifStatement.elseStatement)
            indentLevel--
        }
        
        indent()
        output.appendLine("end")
    }
    
    private fun generateExpression(expression: Expression) {
        when (expression) {
            is StringLiteral -> output.append("\"${expression.value}\"")
            is NumberLiteral -> output.append(expression.value)
            is BooleanLiteral -> output.append(if (expression.value) "true" else "false")
            is Identifier -> output.append(expression.name)
            is FunctionCall -> {
                output.append("${expression.name}(")
                expression.arguments.forEachIndexed { index, arg ->
                    if (index > 0) output.append(", ")
                    generateExpression(arg)
                }
                output.append(")")
            }
            is BinaryOperation -> {
                generateExpression(expression.left)
                output.append(" ${mapOperator(expression.operator)} ")
                generateExpression(expression.right)
            }
        }
    }
    
    private fun mapOperator(kotlinOperator: String): String {
        return when (kotlinOperator) {
            "==" -> "=="
            "!=" -> "~="
            "&&" -> "and"
            "||" -> "or"
            else -> kotlinOperator // Most operators are the same
        }
    }
    
    private fun indent() {
        repeat(indentLevel) {
            output.append("    ") // 4 spaces per indent level
        }
    }
}