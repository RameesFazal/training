package com.example.kotlintraining.designpattern.abstractfactory

interface Button {
    fun paint()
}

interface Checkbox {
    fun paint()
}

interface GUIFactory {
    fun createButton(): Button
    fun createCheckbox(): Checkbox
}

class MacOSButton : Button {
    override fun paint() {
        println("You have created MacOS Button.")
    }
}

class WindowsButton : Button {
    override fun paint() {
        println("You have created Windows Button.")
    }
}

class MacOSCheckbox : Checkbox {
    override fun paint() {
        println("You have created MacOSCheckbox")
    }
}

class WindowsCheckbox : Checkbox {
    override fun paint() {
        println("You have created WindowsCheckbox")
    }
}

class Application(factory: GUIFactory) {
    private val button: Button = factory.createButton()
    private val checkbox: Checkbox = factory.createCheckbox()
    fun paint() {
        button.paint()
        checkbox.paint()
    }
}

class MacOSFactory : GUIFactory {

    override fun createButton(): Button = MacOSButton()

    override fun createCheckbox(): Checkbox = MacOSCheckbox()
}

class WindowsFactory : GUIFactory {

    override fun createButton(): Button = WindowsButton()

    override fun createCheckbox(): Checkbox = WindowsCheckbox()
}

fun configureApplication(): Application {
    val app: Application
    val factory: GUIFactory
    val osName = System.getProperty("os.name").lowercase()
    if (osName.contains("mac")) {
        factory = MacOSFactory()
        app = Application(factory)
    } else {
        factory = WindowsFactory()
        app = Application(factory)
    }
    return app
}

fun main() {
    val app = configureApplication()
    app.paint()
}
