package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis;

public class TimeSeriesXaxis extends xAxis{
    private Boolean boundaryGap;
    public void setAxis(String _type, Boolean _boundaryGap) {
        type = _type;
        boundaryGap = _boundaryGap;
    }
}
