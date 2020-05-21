package com.anwesh.uiprojects.bistrokeboxballview

/**
 * Created by anweshmishra on 22/05/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val parts : Int = 3
val scGap : Float = 0.03f / parts
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#4CAF50")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawStrokedBoxPart(i : Int, sf : Float, w : Float, size : Float, paint : Paint) {
    val sfi : Float = sf.divideScale(i, parts)
    save()
    scale(1f - 2 * i, 1f)
    translate(size + (w / 2 - size) * sfi, 0f)
    drawLine(0f, -size, 0f, size, paint)
    for (j in 0..1) {
        save()
        translate(0f, -size + 2 * size * i)
        drawLine(0f, 0f, -size, 0f, paint)
        restore()
    }
    restore()
}

fun Canvas.drawStrokedBoxBall(scale : Float, w : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    for (j in 0..1) {
        drawStrokedBoxPart(j, sf, w, size, paint)
    }
    drawCircle(0f, 0f, size * sf, paint)
}

fun Canvas.drawSBBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(w / 2, gap * (i + 1))
    drawStrokedBoxBall(scale, w, size, paint)
    restore()
}

class BiStrokedBoxBallView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BSBNode(var i : Int, val state : State = State()) {

        private var next : BSBNode? = null
        private var prev : BSBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BSBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSBBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BSBNode {
            var curr : BSBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BiStrokeBoxBall(var i : Int) {

        private val root : BSBNode = BSBNode(0)
        private var curr : BSBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BiStrokedBoxBallView) {

        private val animator : Animator = Animator(view)
        private val bsb : BiStrokeBoxBall = BiStrokeBoxBall(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            bsb.draw(canvas, paint)
            animator.animate {
                bsb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bsb.startUpdating {
                animator.start()
            }
        }
    }
}