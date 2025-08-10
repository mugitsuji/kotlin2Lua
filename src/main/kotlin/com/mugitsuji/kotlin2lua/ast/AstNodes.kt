package com.mugitsuji.kotlin2lua.ast

/**
 * Base class for all AST nodes
 */
abstract class AstNode

/**
 * Represents a Kotlin program (compilation unit)
 */
data class Program(val declarations: List<Declaration>) : AstNode()

/**
 * Base class for declarations
 */
abstract class Declaration : AstNode()

/**
 * Function declaration
 */
data class FunctionDeclaration(
    val name: String,
    val parameters: List<Parameter>,
    val returnType: String?,
    val body: Block
) : Declaration()

/**
 * Variable declaration
 */
data class VariableDeclaration(
    val name: String,
    val type: String?,
    val initializer: Expression?
) : Declaration()

/**
 * Function parameter
 */
data class Parameter(val name: String, val type: String)

/**
 * Base class for statements
 */
abstract class Statement : AstNode()

/**
 * Block statement containing multiple statements
 */
data class Block(val statements: List<Statement>) : Statement()

/**
 * Expression statement
 */
data class ExpressionStatement(val expression: Expression) : Statement()

/**
 * Return statement
 */
data class ReturnStatement(val expression: Expression?) : Statement()

/**
 * Variable declaration as a statement
 */
data class VariableStatement(val declaration: VariableDeclaration) : Statement()
data class IfStatement(
    val condition: Expression,
    val thenStatement: Statement,
    val elseStatement: Statement?
) : Statement()

/**
 * Base class for expressions
 */
abstract class Expression : AstNode()

/**
 * String literal
 */
data class StringLiteral(val value: String) : Expression()

/**
 * Number literal
 */
data class NumberLiteral(val value: String) : Expression()

/**
 * Boolean literal
 */
data class BooleanLiteral(val value: Boolean) : Expression()

/**
 * Identifier reference
 */
data class Identifier(val name: String) : Expression()

/**
 * Function call
 */
data class FunctionCall(val name: String, val arguments: List<Expression>) : Expression()

/**
 * Binary operation
 */
data class BinaryOperation(
    val left: Expression,
    val operator: String,
    val right: Expression
) : Expression()