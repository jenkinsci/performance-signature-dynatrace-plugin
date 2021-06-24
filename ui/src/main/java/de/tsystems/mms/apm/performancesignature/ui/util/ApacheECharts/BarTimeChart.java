package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis.BarChartsTimeXaxis;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis.BarChartsTimeYaxis;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.Title;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.ToolTip;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.series.BarTimeSeries;

public class BarTimeChart {
    Title title = new Title();
    BarChartsTimeXaxis xAxis = new BarChartsTimeXaxis();
    BarChartsTimeYaxis yAxis = new BarChartsTimeYaxis();
    BarTimeSeries series  = new BarTimeSeries();
    ToolTip tooltip=new ToolTip();

    public BarTimeChart() {
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public BarChartsTimeXaxis getxAxis() {
        return xAxis;
    }

    public void setxAxis(BarChartsTimeXaxis xAxis) {
        this.xAxis = xAxis;
    }

    public BarChartsTimeYaxis getyAxis() {
        return yAxis;
    }

    public void setyAxis(BarChartsTimeYaxis yAxis) {
        this.yAxis = yAxis;
    }

    public BarTimeSeries getSeries() {
        return series;
    }

    public void setSeries(BarTimeSeries series) {
        this.series = series;
    }

    public ToolTip getTooltip() {
        return tooltip;
    }

    public void setTooltip(ToolTip tooltip) {
        this.tooltip = tooltip;
    }

}
