package com.mugitsuji.kotlin2lua.parser

/**
 * Token types for the Kotlin lexer
 */
enum class TokenType {
    // Literals
    STRING,
    NUMBER,
    BOOLEAN,
    
    // Identifiers and keywords
    IDENTIFIER,
    FUN,
    VAL,
    VAR,
    IF,
    ELSE,
    RETURN,
    TRUE,
    FALSE,
    
    // Operators
    ASSIGN,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    EQUALS,
    NOT_EQUALS,
    LESS_THAN,
    GREATER_THAN,
    LESS_EQUAL,
    GREATER_EQUAL,
    
    // Punctuation
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    COMMA,
    COLON,
    SEMICOLON,
    
    // Special
    EOF,
    NEWLINE
}

/**
 * Represents a token in the source code
 */
data class Token(
    val type: TokenType,
    val value: String,
    val line: Int,
    val column: Int
)

/**
 * Simple lexer for Kotlin code
 */
class Lexer(private val source: String) {
    private var position = 0
    private var line = 1
    private var column = 1
    
    private val keywords = mapOf(
        "fun" to TokenType.FUN,
        "val" to TokenType.VAL,
        "var" to TokenType.VAR,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "return" to TokenType.RETURN,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE
    )
    
    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        
        while (!isAtEnd()) {
            skipWhitespace()
            if (isAtEnd()) break
            
            val token = nextToken()
            if (token != null) {
                tokens.add(token)
            }
        }
        
        tokens.add(Token(TokenType.EOF, "", line, column))
        return tokens
    }
    
    private fun nextToken(): Token? {
        val start = position
        val startLine = line
        val startColumn = column
        
        val char = advance()
        
        return when (char) {
            '(' -> Token(TokenType.LEFT_PAREN, "(", startLine, startColumn)
            ')' -> Token(TokenType.RIGHT_PAREN, ")", startLine, startColumn)
            '{' -> Token(TokenType.LEFT_BRACE, "{", startLine, startColumn)
            '}' -> Token(TokenType.RIGHT_BRACE, "}", startLine, startColumn)
            ',' -> Token(TokenType.COMMA, ",", startLine, startColumn)
            ':' -> Token(TokenType.COLON, ":", startLine, startColumn)
            ';' -> Token(TokenType.SEMICOLON, ";", startLine, startColumn)
            '+' -> Token(TokenType.PLUS, "+", startLine, startColumn)
            '-' -> Token(TokenType.MINUS, "-", startLine, startColumn)
            '*' -> Token(TokenType.MULTIPLY, "*", startLine, startColumn)
            '/' -> Token(TokenType.DIVIDE, "/", startLine, startColumn)
            '<' -> {
                if (peek() == '=') {
                    advance()
                    Token(TokenType.LESS_EQUAL, "<=", startLine, startColumn)
                } else {
                    Token(TokenType.LESS_THAN, "<", startLine, startColumn)
                }
            }
            '>' -> {
                if (peek() == '=') {
                    advance()
                    Token(TokenType.GREATER_EQUAL, ">=", startLine, startColumn)
                } else {
                    Token(TokenType.GREATER_THAN, ">", startLine, startColumn)
                }
            }
            '=' -> {
                if (peek() == '=') {
                    advance()
                    Token(TokenType.EQUALS, "==", startLine, startColumn)
                } else {
                    Token(TokenType.ASSIGN, "=", startLine, startColumn)
                }
            }
            '!' -> {
                if (peek() == '=') {
                    advance()
                    Token(TokenType.NOT_EQUALS, "!=", startLine, startColumn)
                } else {
                    throw RuntimeException("Unexpected character '!' at line $startLine, column $startColumn")
                }
            }
            '"' -> string(startLine, startColumn)
            '\n' -> {
                Token(TokenType.NEWLINE, "\n", startLine, startColumn)
            }
            else -> {
                if (char.isDigit()) {
                    number(start, startLine, startColumn)
                } else if (char.isLetter() || char == '_') {
                    identifier(start, startLine, startColumn)
                } else {
                    throw RuntimeException("Unexpected character '$char' at line $startLine, column $startColumn")
                }
            }
        }
    }
    
    private fun string(line: Int, column: Int): Token {
        val value = StringBuilder()
        
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') {
                this.line++
                this.column = 1
            }
            value.append(advance())
        }
        
        if (isAtEnd()) {
            throw RuntimeException("Unterminated string at line $line, column $column")
        }
        
        // Consume closing quote
        advance()
        
        return Token(TokenType.STRING, value.toString(), line, column)
    }
    
    private fun number(start: Int, line: Int, column: Int): Token {
        while (!isAtEnd() && (peek().isDigit() || peek() == '.')) {
            advance()
        }
        
        val value = source.substring(start, position)
        return Token(TokenType.NUMBER, value, line, column)
    }
    
    private fun identifier(start: Int, line: Int, column: Int): Token {
        while (!isAtEnd() && (peek().isLetterOrDigit() || peek() == '_')) {
            advance()
        }
        
        val value = source.substring(start, position)
        val type = keywords[value] ?: TokenType.IDENTIFIER
        
        return Token(type, value, line, column)
    }
    
    private fun skipWhitespace() {
        while (!isAtEnd()) {
            when (peek()) {
                ' ', '\r', '\t' -> advance()
                else -> break
            }
        }
    }
    
    private fun advance(): Char {
        val char = source[position]
        position++
        if (char == '\n') {
            line++
            column = 1
        } else {
            column++
        }
        return char
    }
    
    private fun peek(): Char {
        return if (isAtEnd()) '\u0000' else source[position]
    }
    
    private fun isAtEnd(): Boolean {
        return position >= source.length
    }
}