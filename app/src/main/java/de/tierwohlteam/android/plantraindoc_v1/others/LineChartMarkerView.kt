package de.tierwohlteam.android.plantraindoc_v1.others

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ChartPoint

class LineChartMarkerView(
    private val points: List<ChartPoint>,
    c: Context,
    layoutID: Int) : MarkerView(c, layoutID) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null) return
        val xValue = e.x.toInt()
        val point = points.first{it.xValue == xValue}
        val annotation = if(point.annotation == "") "No annotation" else point.annotation
        val tvCriterion = findViewById<TextView>(R.id.tv_criterion)
        tvCriterion.text = annotation

    }
}