package de.tsystems.mms.apm.performancesignature.ui.util;

import java.util.List;

public class ECharts {

    private class xAxis {
        private String type;
    }

    private class yAxis {
        private String type;
    }


    private class series {

        //        private Integer[] data;
        List<List<Object>> data;
        private Boolean showBackground=true;
        private String type;
        private String color;
    }

    private class title {
        private String text;
        private String left;
        private class textStyle {
            public String fontStyle;
            public String fontWeight;
            public String fontFamily;
            public Integer fontSize;
            public String  overflow;
            public Integer width;
        }

        private textStyle textStyle = new textStyle();
    }
    public static class tooltip {
        private String trigger="item";
    }

    private title title = new title();
    private xAxis xAxis = new xAxis();
    private yAxis yAxis = new yAxis();
    private series series = new series();
    private tooltip tooltip=new tooltip();

    public void setYaxis(String _type) {
        yAxis.type = _type;
    }

    public void setSeries(List<List<Object>> _data, String _type, String _color) {
        series.data = _data;
        series.type = _type;
        series.color = _color;
    }

    public void setXaxis(String _type) {

       xAxis.type=_type;

    }

    public void setTitle(String _title, String _left) {
        _title=_title.replace(" - ", "\n");
        title.text = _title;
        title.left = _left;
        title.textStyle.fontStyle = "normal";
        title.textStyle.fontFamily = "sans-serif";
        title.textStyle.fontSize = 15;
        title.textStyle.fontWeight = "normal";
        title.textStyle.width=450;
        title.textStyle.overflow="break";
    }

}
