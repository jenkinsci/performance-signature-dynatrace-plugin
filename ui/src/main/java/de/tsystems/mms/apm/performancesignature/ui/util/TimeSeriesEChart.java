package de.tsystems.mms.apm.performancesignature.ui.util;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

public class TimeSeriesEChart {
    public static class xAxis {
        private String type;
        private Boolean boundaryGap;
    }

    private class yAxis {
        private String type;
        private Double axisMinimum;
        private Double axisMaximum;

    }

    private class series {
        private List<List<Object>> data;
        private Boolean showBackground = true;
        private String color;
        private String type;
        private Boolean smooth;
        private String symbol;
    }

    private class title {
        private String left;
        private String text;

        private class textStyle {
            public String fontStyle;
            public String fontWeight;
            public String fontFamily;
            public Integer fontSize;
        }

        private textStyle textStyle = new textStyle();
    }

    private class tooltip {
        private String trigger = "axis";
    }

    private tooltip tooltip = new tooltip();
    private title title = new title();
    private xAxis xAxis = new xAxis();
    private yAxis yAxis = new yAxis();
    private series series = new series();

    public void setYaxis(String _type, Double _axisMin, Double _axisMax) {

        yAxis.type = _type;
        yAxis.axisMinimum=_axisMin;
        yAxis.axisMaximum=_axisMax;


    }

    public void setSeries(List<List<Object>> _data, String _type, Boolean _smooth, String _color) {
        series.data = _data;
        series.color = _color;
        series.type = _type;
        series.smooth = _smooth;
    }

    public void setXaxis(String _type, Boolean _boundaryGap) {
        xAxis.type = _type;
        xAxis.boundaryGap = _boundaryGap;
    }

    public void setTitle(String _title, String _left) {
        title.text = _title;
        title.left = _left;
        title.textStyle.fontStyle = "normal";
        title.textStyle.fontFamily = "sans-serif";
        title.textStyle.fontSize = 15;
        title.textStyle.fontWeight = "normal";
    }
}
