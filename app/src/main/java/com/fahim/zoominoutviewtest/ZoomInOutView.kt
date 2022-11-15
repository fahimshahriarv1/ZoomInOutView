package com.fahim.zoominoutviewtest

import android.annotation.SuppressLint
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.sqrt

@SuppressLint("ClickableViewAccessibility")
class ZoomInOutView(private var view: View?) {

    private var lastEvent: FloatArray? = null
    private var isZoomAndRotate = false
    private var isOutSide = false
    private val noAction = 0
    private val actionZoomIn = 2
    private var mode = noAction
    private val start = PointF()
    private val mid = PointF()
    var oldDist = 1f
    private var xCoOrdinate = 0f
    private var yCoOrdinate = 0f
    private val actionDrag = 1
    private var viewOriginalX: Float = 0f
    private var viewOriginalY: Float = 0f
    private var viewOriginalXR: Float = 0f
    private var viewOriginalYR: Float = 0f

    private fun setOriginalValue() {
        viewOriginalX = ((view!!.parent as View).width / 2 - (view!!.width / 2)).toFloat()
        viewOriginalY = ((view!!.parent as View).height / 2 - (view!!.height / 2)).toFloat()
        val rect = intArrayOf(0, 0)
        view?.getLocationOnScreen(rect)
        viewOriginalX = rect[0].toFloat()
        viewOriginalY = rect[1].toFloat()
//            viewOriginalXR = rect.right.toFloat()
//            viewOriginalYR = rect.bottom.toFloat()
    }


    fun setView(view: View) {
        this.view = view

        view.setOnTouchListener { v: View, event ->
            viewTransformation(v, event)
            true
        }
        setOriginalValue()
    }

    private fun viewTransformation(view: View, event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xCoOrdinate = view.x - event.rawX
                yCoOrdinate = view.y - event.rawY
                start[event.x] = event.y
                isOutSide = false
                mode = actionDrag
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    midPoint(mid, event)
                    mode = actionZoomIn
                }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
            }
            MotionEvent.ACTION_UP -> {
                isZoomAndRotate = false
                isOutSide = true
                mode = noAction
                lastEvent = null
                mode = noAction
                lastEvent = null
            }
            MotionEvent.ACTION_OUTSIDE -> {
                isOutSide = true
                mode = noAction
                lastEvent = null
                mode = noAction
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = noAction
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> if (!isOutSide) {
                if (mode == actionDrag) {
                    isZoomAndRotate = false
//                    if (!(event.rawX + xCoOrdinate > view.x*view.scaleX) ||!(event.rawY + yCoOrdinate <= view.y*view.scaleX))
                    var x = event.rawX + xCoOrdinate
                    var y = event.rawY + yCoOrdinate
                    if (view.scaleX <= 1f) {
                        if (x < 0 || view.x < 0)
                            x = 0f
                        if (y < 0 || view.y < 0)
                            y = 0f
                        if (x > ((view.parent as View).width - view.width) || view.x > ((view.parent as View).width - view.width))
                            x = ((view.parent as View).width - view.width).toFloat()
                        if (y > ((view.parent as View).height - view.height) || view.y > ((view.parent as View).height - view.height))
                            y = ((view.parent as View).height - view.height).toFloat()
                        view.animate().x(x).y(y).setDuration(0).start()
                    } else {
                        val diffW = abs((view.parent as View).width - view.width)
                        val diffH = abs((view.parent as View).height - view.height)
                        if (x < 0 - diffW || view.x < 0 - diffW)
                            x = (0 - diffW).toFloat()
                        if (y < 0 - diffH || view.y < 0 - diffH)
                            y = (0 - diffH).toFloat()
                        if (x > ((view.parent as View).width - view.width) || view.x > ((view.parent as View).width - view.width))
                            x = ((view.parent as View).width - view.width).toFloat()
                        if (y > ((view.parent as View).height - view.height) || view.y > ((view.parent as View).height - view.height))
                            y = ((view.parent as View).height - view.height).toFloat()
                        view.animate().x(x).y(y).setDuration(0).start()
                    }
                }

                if (mode == actionZoomIn && event.pointerCount == 2) {
                    val newDist1 = spacing(event)
                    if (newDist1 > 10f) {
                        val scale = newDist1 / oldDist * view.scaleX
                        if (scale in 1.0..7.0) {
                            view.scaleX = scale
                            view.scaleY = scale
                        }
                    }
                }

                android.util.Log.i("---->original X", viewOriginalX.toString())
                android.util.Log.i("---->original Y", viewOriginalY.toString())
                android.util.Log.i("---->original XR", viewOriginalXR.toString())
                android.util.Log.i("---->original YR", viewOriginalYR.toString())
                android.util.Log.i("---->current X", view.x.toString())
                android.util.Log.i("---->current Y", view.y.toString())
                android.util.Log.i("---->originalScale", 1.toString())
                android.util.Log.i("---->currentScale", view.scaleX.toString())
                android.util.Log.i("---->width", view.width.toString())
                android.util.Log.i("---->height", view.height.toString())
                android.util.Log.i("---->ParentWidth", (view.parent as View).width.toString())
                android.util.Log.i("---->ParentHeight", (view.parent as View).height.toString())
                android.util.Log.i("---------------", "------------------------")
            }
        }
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

}