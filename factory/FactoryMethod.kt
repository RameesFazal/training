package com.example.kotlintraining.designpattern.factory

interface Button {
    fun onClick()
}

class HtmlButton : Button {
    override fun onClick() = print("Clicked Html Button");
}

class WindowButton : Button {
    override fun onClick() = print("Clicked Window Button");
}

abstract class Dialog {

    fun renderWindow() {
        val okButton: Button = createButton()
        okButton.onClick()
    }

    abstract fun createButton(): Button
}

class HtmlDialog : Dialog() {
    override fun createButton(): Button {
        return HtmlButton();
    }
}

class WindowsDialog : Dialog() {
    override fun createButton(): Button {
        return WindowButton()
    }
}

class FactoryMethod {
    lateinit var dialog: Dialog

    fun configure() {
        val osName = System.getProperty("os.name")
        osName?.let {
            dialog = if (it == "Windows 10") {
                WindowsDialog()
            } else {
                HtmlDialog()
            }
        } ?: run {
            dialog = HtmlDialog()
        }
    }

    fun runBusinessLogic() {
        dialog.renderWindow()
    }
}

fun main() {
    val factoryMethod = FactoryMethod()
    factoryMethod.configure()
    factoryMethod.runBusinessLogic()
}