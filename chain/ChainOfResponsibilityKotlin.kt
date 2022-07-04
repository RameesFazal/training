package com.example.kotlintraining.designpattern.chain

import java.io.BufferedReader
import java.io.InputStreamReader

abstract class Middleware {
    private var next: Middleware? = null

    fun linkWith(next: Middleware?): Middleware? {
        this.next = next
        return next
    }

    abstract fun check(email: String, password: String): Boolean

    fun checkNext(email: String, password: String): Boolean {
        return if (next == null) {
            true
        } else next!!.check(email, password)
    }
}

class RoleCheckMiddleware : Middleware() {
    override fun check(email: String, password: String): Boolean {
        if (email == "admin@example.com") {
            println("Hello, admin!")
            return true
        }
        println("Hello, user!")
        return checkNext(email, password)
    }
}


class ThrottlingMiddleware(private val requestPerMinute: Int) : Middleware() {
    var request = 0
    var currentTime: Long
    override fun check(email: String, password: String): Boolean {
        if (System.currentTimeMillis() > currentTime + 60000) {
            request = 0
            currentTime = System.currentTimeMillis()
        }
        request++
        if (request > requestPerMinute) {
            println("Request limit exceeded!")
            Thread.currentThread().stop()
        }
        return checkNext(email, password)
    }

    init {
        currentTime = System.currentTimeMillis()
    }
}

class UserExistsMiddleware(val server: Server) : Middleware() {
    override fun check(email: String, password: String): Boolean {
        if (!server.hasEmail(email)) {
            println("This email is not registered!")
            return false
        }
        if (!server.isValidPassword(email, password)) {
            println("Wrong password!")
            return false
        }
        return checkNext(email, password)
    }
}

class Server {
    private val users: MutableMap<String, String> = HashMap()
    private var middleware: Middleware? = null

    fun setMiddleware(middleware: Middleware?) {
        this.middleware = middleware
    }

    fun logIn(email: String, password: String): Boolean {
        if (middleware!!.check(email, password)) {
            println("Authorization have been successful!")
            return true
        }
        return false
    }

    fun register(email: String, password: String) {
        users[email] = password
    }

    fun hasEmail(email: String): Boolean {
        return users.containsKey(email)
    }

    fun isValidPassword(email: String, password: String): Boolean {
        return users[email] == password
    }
}

object ChainOfResponsibility {
    val reader = BufferedReader(InputStreamReader(System.`in`))
    var server: Server? = null
    fun init() {
        server = Server()
        server!!.register("admin@example.com", "admin_pass")
        server!!.register("user@example.com", "user_pass")

        val middleware: Middleware = ThrottlingMiddleware(2)
        middleware.linkWith(
            UserExistsMiddleware(
                server!!
            )
        )
        server!!.setMiddleware(middleware)
    }
}

fun main() {
    ChainOfResponsibility.init()
    var success: Boolean
    do {
        print("Enter email: ")
        val email = ChainOfResponsibility.reader.readLine()
        print("Input password: ")
        val password = ChainOfResponsibility.reader.readLine()
        success = ChainOfResponsibility.server!!.logIn(email, password)
    } while (!success)
}