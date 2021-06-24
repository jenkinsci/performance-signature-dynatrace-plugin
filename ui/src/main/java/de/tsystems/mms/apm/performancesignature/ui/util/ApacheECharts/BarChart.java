package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis.BarChartXaxis;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis.BarChartYaxis;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis.BarChartsTimeXaxis;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis.BarChartsTimeYaxis;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.Title;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.ToolTip;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.series.BarSeries;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.series.BarTimeSeries;

public class BarChart {
    Title title = new Title();
    BarChartXaxis xAxis = new BarChartXaxis();
    BarChartYaxis yAxis = new BarChartYaxis();
    BarSeries series  = new BarSeries();
    ToolTip tooltip=new ToolTip();
    String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public BarChartXaxis getxAxis() {
        return xAxis;
    }

    public void setxAxis(BarChartXaxis xAxis) {
        this.xAxis = xAxis;
    }

    public BarChartYaxis getyAxis() {
        return yAxis;
    }

    public void setyAxis(BarChartYaxis yAxis) {
        this.yAxis = yAxis;
    }

    public BarSeries getSeries() {
        return series;
    }

    public void setSeries(BarSeries series) {
        this.series = series;
    }

    public ToolTip getTooltip() {
        return tooltip;
    }

    public void setTooltip(ToolTip tooltip) {
        this.tooltip = tooltip;
    }
}
