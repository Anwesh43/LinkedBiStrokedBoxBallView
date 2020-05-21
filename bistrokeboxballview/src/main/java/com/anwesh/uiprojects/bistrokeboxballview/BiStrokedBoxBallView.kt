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
}