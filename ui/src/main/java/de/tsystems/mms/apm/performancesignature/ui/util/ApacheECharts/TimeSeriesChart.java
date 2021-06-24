package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis.TimeSeriesXaxis;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis.TimeSeriesYAxis;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.Title;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.ToolTip;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.series.TimeChartSeries;


public class TimeSeriesChart {
    private ToolTip tooltip;
    private Title title;
    private TimeSeriesXaxis xAxis;
    private TimeSeriesYAxis yAxis;
    private TimeChartSeries series;

    public ToolTip getTooltip() {
        return tooltip;
    }

    public void setTooltip(ToolTip tooltip) {
        this.tooltip = tooltip;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public TimeSeriesXaxis getxAxis() {
        return xAxis;
    }

    public void setxAxis(TimeSeriesXaxis xAxis) {
        this.xAxis = xAxis;
    }

    public TimeSeriesYAxis getyAxis() {
        return yAxis;
    }

    public void setyAxis(TimeSeriesYAxis yAxis) {
        this.yAxis = yAxis;
    }

    public TimeChartSeries getSeries() {
        return series;
    }

    public void setSeries(TimeChartSeries series) {
        this.series = series;
    }
}
