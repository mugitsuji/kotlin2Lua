fun fibonacci(n: Int): Int {
    if (n <= 1) {
        return n
    } else {
        return fibonacci(n - 1) + fibonacci(n - 2)
    }
}

fun factorial(n: Int): Int {
    if (n <= 1) {
        return 1
    } else {
        return n * factorial(n - 1)
    }
}

fun main() {
    val fib10 = fibonacci(10)
    val fact5 = factorial(5)
    
    println("Fibonacci of 10: " + fib10)
    println("Factorial of 5: " + fact5)
}