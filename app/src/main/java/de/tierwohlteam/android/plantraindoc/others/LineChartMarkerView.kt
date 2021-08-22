package de.tierwohlteam.android.plantraindoc.others

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import de.tierwohlteam.android.plantraindoc.R
import de.tierwohlteam.android.plantraindoc.viewmodels.ChartPoint

@SuppressLint("ViewConstructor")
class LineChartMarkerView(
    private val points: List<ChartPoint>,
    c: Context,
    layoutID: Int) : MarkerView(c, layoutID) {

 /*   override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    } */

    override fun draw(canvas: Canvas?, posX: Float, posY: Float) {
        if(canvas == null)
            super.draw(canvas, posX, posY)
        else {
            val fixedX = 40F
            val fixedY = 50F
            // translate to the correct position and draw
            // https://stackoverflow.com/questions/32838687/show-marker-view-under-x-axis-at-the-same-height-whatever-the-value-is-using-m
            canvas.translate(fixedX, fixedY)
            draw(canvas)
            canvas.translate(-fixedX, -fixedY)
        }
    }
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null) return
        val xValue = e.x.toInt()
        val point = points.first{it.xValue == xValue}
        val goalAnnotation = if(point.goal == "") "No annotation" else point.goal
        val tvGoal = findViewById<TextView>(R.id.tv_chart_goal)
        tvGoal.text = goalAnnotation
        val sessionAnnotation = if(point.sessionCriterion == "") "No annotation" else point.sessionCriterion
        val tvSession = findViewById<TextView>(R.id.tv_chart_session)
        tvSession.text = sessionAnnotation
    }
}