package observer

import java.io.File


internal class Editor {
    var events: EventManager = EventManager("open", "save")
    private var file: File? = null
    fun openFile(filePath: String?) {
        file = File(filePath)
        events.notify("open", file)
    }

    fun saveFile() {
        if (file != null) {
            events.notify("save", file)
        } else {
            throw Exception("Please open a file first.")
        }
    }

}

class EventManager(vararg operations: String) {

    var listeners: MutableMap<String, MutableList<EventListener>> = HashMap()

    init {
        for (operation in operations) {
            listeners.put(operation, ArrayList())
        }
    }

    fun subscribe(eventType: String, listener: EventListener) {
        val users = listeners[eventType]!!
        users.add(listener)
    }

    fun unsubscribe(eventType: String, listener: EventListener) {
        val users = listeners[eventType]!!
        users.remove(listener)
    }

    fun notify(eventType: String, file: File?) {
        val users: List<EventListener> = listeners[eventType]!!
        for (listener in users) {
        }
    }
}

class EmailNotificationListener(private val email: String) : EventListener {
    override fun update(eventType: String?, file: File?) {
        println("Email to " + email + ": Someone has performed " + eventType + " operation with the following file: " + file?.name)
    }

}

interface EventListener {
    fun update(eventType: String?, file: File?)
}

class LogOpenListener(fileName: String?) : EventListener {
    private val log: File = File(fileName)
    override fun update(eventType: String?, file: File?) {
        println("Save to log " + log + ": Someone has performed " + eventType + " operation with the following file: " + file!!.name)
    }
}

object B06Observer {
    @JvmStatic
    fun main(args: Array<String>) {
        val editor = Editor()
        editor.events.subscribe("open", LogOpenListener("/path/to/log/file.txt"))
        editor.events.subscribe("save", EmailNotificationListener("admin@example.com"))
        try {
            editor.openFile("test.txt")
            editor.saveFile()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}