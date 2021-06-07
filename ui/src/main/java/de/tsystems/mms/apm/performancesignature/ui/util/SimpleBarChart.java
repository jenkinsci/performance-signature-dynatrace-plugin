package de.tsystems.mms.apm.performancesignature.ui.util;

import java.util.List;

public class SimpleBarChart{
    private class xAxis {
        private List<String> data;
        private String type;
        private String name;
        private String nameLocation;
        private int nameGap;

      private class axisLabel{
            public int interval;
            public int rotate;
            public String fontWeight;
        }
        axisLabel axisLabel=new axisLabel();
    }
    private class tooltip {
        private String trigger="item";
    }
    private class yAxis {
        private String type;
        private String name;
        private String nameLocation;
        private int nameGap;
        private class nameTextStyle{
            public String fontWeight;
        }
        nameTextStyle nameTextStyle =new nameTextStyle();
    }
    private class backgroundStyle{
        private String color;
    }

    private class series {
        private List<Double> data;
        private String type;
        private backgroundStyle backgroundStyle=new backgroundStyle();
        private Boolean showBackground;
    }

    private class title {
        private String text;
        private String left;
    }


    private String color;
    private title title = new title();
    private xAxis xAxis = new xAxis();
    private yAxis yAxis = new yAxis();
    private series series = new series();
    private tooltip tooltip=new tooltip();


    public void setYaxis(String _type, String _name, String _nameLocation, int _nameGap) {
        yAxis.type=_type;
        yAxis.name=_name;
        yAxis.nameLocation=_nameLocation;
        yAxis.nameGap=_nameGap;
        yAxis.nameTextStyle.fontWeight="bolder";
    }

    public void setSeries(List<Double> _data, String _type) {
        series.data = _data;
        series.type = _type;
        series.showBackground=true;
        series.backgroundStyle.color="rgba(180, 180, 180, 0.2)";
    }

    public void setXaxis(List<String> _data,String _type, String _name,String _nameLocation, int _nameGap ) {

        xAxis.data=_data;
        xAxis.type=_type;
        xAxis.name=_name;
        xAxis.nameLocation=_nameLocation;
        xAxis.nameGap=_nameGap;
        xAxis.axisLabel.interval=0;
        xAxis.axisLabel.rotate=90;


    }

    public void setColor(String _color)
    {
        color=_color;
    }

    public void setTitle(String _title, String _left) {
        title.text = _title;
        title.left = _left;

    }

}
