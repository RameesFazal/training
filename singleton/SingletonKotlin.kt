package com.example.kotlintraining.designpattern.singleton

class SingletonNonThreadSafe private constructor(value: String) {
    var value: String

    companion object {
        private var instance: SingletonNonThreadSafe? = null
        fun getInstance(value: String): SingletonNonThreadSafe? {
            if (instance == null) {
                instance = SingletonNonThreadSafe(value)
            }
            return instance
        }
    }

    init {
        try {
            Thread.sleep(1000)
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        }
        this.value = value
    }
}


object DemoMultiThreadNonThreadSafe {
    fun playWithThreads() {
        println(
            "If you see the same value, then singleton was reused (yay!)" + "\n" +
                    "If you see different values, then 2 singletons were created (booo!!)" + "\n\n" +
                    "RESULT:" + "\n"
        )
        val threadFoo = Thread(ThreadFoo())
        val threadBar = Thread(ThreadBar())
        threadFoo.start()
        threadBar.start()
    }

    class ThreadFoo() : Runnable {
        override fun run() {
            val singleton = SingletonNonThreadSafe.getInstance("FOO")
            println(singleton!!.value)
        }
    }

    class ThreadBar() : Runnable {
        override fun run() {
            val singleton = SingletonNonThreadSafe.getInstance("BAR")
            println(singleton!!.value)
        }
    }
}

fun main() {
    DemoMultiThreadNonThreadSafe.playWithThreads()
}