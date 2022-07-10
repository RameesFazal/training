package composite

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants


class ImageEditor {
    private val canvas: EditorCanvas = EditorCanvas()
    private val allShapes: CompoundShape = CompoundShape()

    fun loadShapes(shapes: Shape) {
        allShapes.clear()
        allShapes.add(shapes)
        canvas.refresh()
    }

    private companion object {
        private const val PADDING = 10
    }

    private inner class EditorCanvas internal constructor() : Canvas() {
        var frame: JFrame? = null
        fun createFrame() {
            frame = JFrame()
            frame!!.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame!!.setLocationRelativeTo(null)
            val contentPanel = JPanel()
            val padding = BorderFactory.createEmptyBorder(
                PADDING,
                PADDING,
                PADDING,
                PADDING
            )
            contentPanel.border = padding
            frame!!.contentPane = contentPanel
            frame!!.add(this)
            frame!!.isVisible = true
            frame!!.contentPane.background = Color.LIGHT_GRAY
        }

        override fun getWidth(): Int {
            return allShapes.x + allShapes.width + PADDING
        }

        override fun getHeight(): Int {
            return allShapes.y + allShapes.height + PADDING
        }

        fun refresh() {
            this.setSize(width, height)
            frame!!.pack()
        }

        override fun paint(graphics: Graphics) {
            allShapes.paint(graphics)
        }

        init {
            createFrame()
            refresh()
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    allShapes.unSelect()
                    allShapes.selectChildAt(e.x, e.y)
                    e.component.repaint()
                }
            })
        }
    }
}

interface Shape {
    val x: Int
    val y: Int
    val width: Int
    val height: Int

    fun move(x: Int, y: Int)
    fun isInsideBounds(x: Int, y: Int): Boolean
    fun select()
    fun unSelect()
    val isSelected: Boolean

    fun paint(graphics: Graphics?)
}

abstract class BaseShape(override var x: Int, override var y: Int, var color: Color) :
    Shape {
    final override var isSelected = false
        private set
    override val width: Int
        get() = 0
    override val height: Int
        get() = 0

    override fun move(x: Int, y: Int) {
        this.x += x
        this.y += y
    }

    override fun isInsideBounds(x: Int, y: Int): Boolean {
        return x > x && x < x + width && y > y && y < y + height
    }

    override fun select() {
        isSelected = true
    }

    override fun unSelect() {
        isSelected = false
    }

    fun enableSelectionStyle(graphics: Graphics?) {
        graphics!!.color = Color.LIGHT_GRAY
        val g2 = graphics as Graphics2D?
        val dash1 = floatArrayOf(2.0f)
        g2!!.stroke = BasicStroke(
            1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            2.0f, dash1, 0.0f
        )
    }

    fun disableSelectionStyle(graphics: Graphics?) {
        graphics!!.color = color
        val g2 = graphics as Graphics2D?
        g2!!.stroke = BasicStroke()
    }

    override fun paint(graphics: Graphics?) {
        if (isSelected) {
            enableSelectionStyle(graphics)
        } else {
            disableSelectionStyle(graphics)
        }
    }
}

class Circle(x: Int, y: Int, var radius: Int, color: Color?) :
    BaseShape(x, y, color!!) {
    override val width: Int
        get() = radius * 2
    override val height: Int
        get() = radius * 2

    override fun paint(graphics: Graphics?) {
        super.paint(graphics)
        graphics?.drawOval(x, y, width - 1, height - 1)
    }
}


class CompoundShape() : BaseShape(0, 0, Color.BLACK) {
    protected var children: MutableList<Shape> = ArrayList()
    fun add(component: Shape) {
        children.add(component)
    }

    fun add(components: List<Shape>) {
        children.addAll(components)
    }

    fun remove(child: Shape) {
        children.remove(child)
    }

    fun remove(components: List<Shape>) {
        children.removeAll(components)
    }

    fun clear() {
        children.clear()
    }

    override var x: Int
        get() {
            if (children.size == 0) {
                return 0
            }
            var x = children[0].x
            for (child in children) {
                if (child.x < x) {
                    x = child.x
                }
            }
            return x
        }
        set(x) {
            super.x = x
        }
    override var y: Int
        get() {
            if (children.size == 0) {
                return 0
            }
            var y = children[0].y
            for (child in children) {
                if (child.y < y) {
                    y = child.y
                }
            }
            return y
        }
        set(y) {
            super.y = y
        }
    override val width: Int
        get() {
            var maxWidth = 0
            val x = x
            for (child in children) {
                val childsRelativeX = child.x - x
                val childWidth = childsRelativeX + child.width
                if (childWidth > maxWidth) {
                    maxWidth = childWidth
                }
            }
            return maxWidth
        }
    override val height: Int
        get() {
            var maxHeight = 0
            val y = y
            for (child in children) {
                val childsRelativeY = child.y - y
                val childHeight = childsRelativeY + child.height
                if (childHeight > maxHeight) {
                    maxHeight = childHeight
                }
            }
            return maxHeight
        }

    override fun move(x: Int, y: Int) {
        for (child in children) {
            child.move(x, y)
        }
    }

    override fun isInsideBounds(x: Int, y: Int): Boolean {
        for (child in children) {
            if (child.isInsideBounds(x, y)) {
                return true
            }
        }
        return false
    }

    override fun unSelect() {
        super.unSelect()
        for (child in children) {
            child.unSelect()
        }
    }

    fun selectChildAt(x: Int, y: Int): Boolean {
        for (child in children) {
            if (child.isInsideBounds(x, y)) {
                child.select()
                return true
            }
        }
        return false
    }

    override fun paint(graphics: Graphics?) {
        if (isSelected) {
            enableSelectionStyle(graphics)
            graphics!!.drawRect(x - 1, y - 1, width + 1, height + 1)
            disableSelectionStyle(graphics)
        }
        for (child in children) {
            child.paint(graphics)
        }
    }
}

class Dot(x: Int, y: Int, color: Color?) : BaseShape(x, y, color!!) {
    override val height = 3

    override fun paint(graphics: Graphics?) {
        super.paint(graphics)
        graphics!!.fillRect(x - 1, y - 1, height, height)
    }
}

class Rectangle(
    x: Int,
    y: Int,
    override var width: Int,
    override var height: Int,
    color: Color?
) :
    BaseShape(x, y, color!!) {

    override fun paint(graphics: Graphics?) {
        super.paint(graphics)
        graphics!!.drawRect(x, y, width - 1, height - 1)
    }
}

object S03Composite {
    @JvmStatic
    fun main(args: Array<String>) {
        val editor = ImageEditor()
        editor.loadShapes(
            Circle(10, 10, 10, Color.BLUE),
        )
    }
}