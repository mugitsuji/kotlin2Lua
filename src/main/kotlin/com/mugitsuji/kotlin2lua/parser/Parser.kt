package com.mugitsuji.kotlin2lua.parser

import com.mugitsuji.kotlin2lua.ast.*

/**
 * Simple recursive descent parser for Kotlin code
 */
class Parser(private val tokens: List<Token>) {
    private var current = 0
    
    fun parse(): Program {
        val declarations = mutableListOf<Declaration>()
        
        while (!isAtEnd()) {
            // Skip newlines at top level
            if (check(TokenType.NEWLINE)) {
                advance()
                continue
            }
            
            val declaration = parseDeclaration()
            if (declaration != null) {
                declarations.add(declaration)
            }
        }
        
        return Program(declarations)
    }
    
    private fun parseDeclaration(): Declaration? {
        return when {
            match(TokenType.FUN) -> parseFunction()
            match(TokenType.VAL, TokenType.VAR) -> parseVariable()
            else -> {
                throw RuntimeException("Expected declaration at ${peek().line}:${peek().column}")
            }
        }
    }
    
    private fun parseFunction(): FunctionDeclaration {
        val name = consume(TokenType.IDENTIFIER, "Expected function name").value
        
        consume(TokenType.LEFT_PAREN, "Expected '(' after function name")
        
        val parameters = mutableListOf<Parameter>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                val paramName = consume(TokenType.IDENTIFIER, "Expected parameter name").value
                consume(TokenType.COLON, "Expected ':' after parameter name")
                val paramType = consume(TokenType.IDENTIFIER, "Expected parameter type").value
                parameters.add(Parameter(paramName, paramType))
            } while (match(TokenType.COMMA))
        }
        
        consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters")
        
        // Optional return type
        var returnType: String? = null
        if (match(TokenType.COLON)) {
            returnType = consume(TokenType.IDENTIFIER, "Expected return type").value
        }
        
        val body = parseBlock()
        
        return FunctionDeclaration(name, parameters, returnType, body)
    }
    
    private fun parseVariable(): VariableDeclaration {
        val name = consume(TokenType.IDENTIFIER, "Expected variable name").value
        
        var type: String? = null
        if (match(TokenType.COLON)) {
            type = consume(TokenType.IDENTIFIER, "Expected variable type").value
        }
        
        var initializer: Expression? = null
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression()
        }
        
        return VariableDeclaration(name, type, initializer)
    }
    
    private fun parseBlock(): Block {
        consume(TokenType.LEFT_BRACE, "Expected '{'")
        
        val statements = mutableListOf<Statement>()
        
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            // Skip newlines inside blocks
            if (check(TokenType.NEWLINE)) {
                advance()
                continue
            }
            
            val statement = parseStatement()
            if (statement != null) {
                statements.add(statement)
            }
        }
        
        consume(TokenType.RIGHT_BRACE, "Expected '}'")
        
        return Block(statements)
    }
    
    private fun parseStatement(): Statement? {
        return when {
            match(TokenType.RETURN) -> parseReturn()
            match(TokenType.IF) -> parseIf()
            match(TokenType.VAL, TokenType.VAR) -> {
                // Variable declaration as a statement
                val varDecl = parseVariable()
                VariableStatement(varDecl)
            }
            match(TokenType.LEFT_BRACE) -> {
                // Put back the brace and parse as block
                current--
                parseBlock()
            }
            else -> {
                // Expression statement
                val expr = parseExpression()
                ExpressionStatement(expr)
            }
        }
    }
    
    private fun parseReturn(): ReturnStatement {
        var expression: Expression? = null
        if (!check(TokenType.NEWLINE) && !check(TokenType.RIGHT_BRACE)) {
            expression = parseExpression()
        }
        return ReturnStatement(expression)
    }
    
    private fun parseIf(): IfStatement {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'")
        val condition = parseExpression()
        consume(TokenType.RIGHT_PAREN, "Expected ')' after if condition")
        
        val thenStatement = parseStatement()!!
        
        var elseStatement: Statement? = null
        if (match(TokenType.ELSE)) {
            elseStatement = parseStatement()
        }
        
        return IfStatement(condition, thenStatement, elseStatement)
    }
    
    private fun parseExpression(): Expression {
        return parseEquality()
    }
    
    private fun parseEquality(): Expression {
        var expr = parseComparison()
        
        while (match(TokenType.EQUALS, TokenType.NOT_EQUALS)) {
            val operator = previous().value
            val right = parseComparison()
            expr = BinaryOperation(expr, operator, right)
        }
        
        return expr
    }
    
    private fun parseComparison(): Expression {
        var expr = parseTerm()
        
        while (match(TokenType.GREATER_THAN, TokenType.LESS_THAN, TokenType.GREATER_EQUAL, TokenType.LESS_EQUAL)) {
            val operator = previous().value
            val right = parseTerm()
            expr = BinaryOperation(expr, operator, right)
        }
        
        return expr
    }
    
    private fun parseTerm(): Expression {
        var expr = parseFactor()
        
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous().value
            val right = parseFactor()
            expr = BinaryOperation(expr, operator, right)
        }
        
        return expr
    }
    
    private fun parseFactor(): Expression {
        var expr = parsePrimary()
        
        while (match(TokenType.DIVIDE, TokenType.MULTIPLY)) {
            val operator = previous().value
            val right = parsePrimary()
            expr = BinaryOperation(expr, operator, right)
        }
        
        return expr
    }
    
    private fun parsePrimary(): Expression {
        when {
            match(TokenType.TRUE) -> return BooleanLiteral(true)
            match(TokenType.FALSE) -> return BooleanLiteral(false)
            
            match(TokenType.NUMBER) -> return NumberLiteral(previous().value)
            
            match(TokenType.STRING) -> return StringLiteral(previous().value)
            
            match(TokenType.IDENTIFIER) -> {
                val name = previous().value
                // Check if it's a function call
                if (match(TokenType.LEFT_PAREN)) {
                    val arguments = mutableListOf<Expression>()
                    if (!check(TokenType.RIGHT_PAREN)) {
                        do {
                            arguments.add(parseExpression())
                        } while (match(TokenType.COMMA))
                    }
                    consume(TokenType.RIGHT_PAREN, "Expected ')' after function arguments")
                    return FunctionCall(name, arguments)
                }
                return Identifier(name)
            }
            
            match(TokenType.LEFT_PAREN) -> {
                val expr = parseExpression()
                consume(TokenType.RIGHT_PAREN, "Expected ')' after expression")
                return expr
            }
        }
        
        throw RuntimeException("Unexpected token ${peek().value} at ${peek().line}:${peek().column}")
    }
    
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }
    
    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }
    
    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }
    
    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.EOF
    }
    
    private fun peek(): Token {
        return tokens[current]
    }
    
    private fun previous(): Token {
        return tokens[current - 1]
    }
    
    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw RuntimeException("$message at ${peek().line}:${peek().column}")
    }
}