# Kotlin2Lua

A compiler that transpiles Kotlin code to Lua.

## Features

This transpiler supports the following Kotlin language constructs:

- **Functions**: Function declarations with parameters and return types
- **Variables**: `val` and `var` declarations with optional type annotations  
- **Control Flow**: `if`/`else` statements
- **Expressions**: Binary operations, function calls, literals
- **Data Types**: Numbers, strings, booleans

## Usage

### Command Line

```bash
# Transpile a Kotlin file to Lua
./build/install/kotlin2lua/bin/kotlin2lua input.kt [output.lua]
```

### Examples

Input Kotlin code:
```kotlin
fun greet(name: String): String {
    return "Hello, " + name + "!"
}

fun main() {
    val greeting = greet("World")
    println(greeting)
}
```

Generated Lua code:
```lua
function greet(name)
    return "Hello, " + name + "!"
end
function main()
    local greeting = greet("World")
    println(greeting)
end
```

## Building

```bash
# Build the project
./gradlew build

# Install distribution (creates CLI executable)
./gradlew installDist

# Run tests
./gradlew test
```

## Architecture

The transpiler consists of several components:

1. **Lexer** (`parser/Lexer.kt`): Tokenizes Kotlin source code
2. **Parser** (`parser/Parser.kt`): Builds an Abstract Syntax Tree (AST) from tokens
3. **AST Nodes** (`ast/AstNodes.kt`): Represents Kotlin language constructs
4. **Lua Generator** (`generator/LuaGenerator.kt`): Converts AST to Lua code
5. **Transpiler** (`Transpiler.kt`): Orchestrates the transpilation process
6. **CLI** (`Main.kt`): Command-line interface

## Supported Kotlin Syntax

### Function Declaration
```kotlin
fun functionName(param1: Type, param2: Type): ReturnType {
    // body
}
```

### Variable Declaration
```kotlin
val immutableVar = value
var mutableVar: Type = value
```

### Control Flow
```kotlin
if (condition) {
    // then block
} else {
    // else block
}
```

### Expressions
```kotlin
// Binary operations
a + b, a - b, a * b, a / b
a == b, a != b, a < b, a > b

// Function calls
functionName(arg1, arg2)

// Literals
42, "string", true, false
```