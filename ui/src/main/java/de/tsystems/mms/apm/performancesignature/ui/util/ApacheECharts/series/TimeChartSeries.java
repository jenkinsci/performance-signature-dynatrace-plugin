package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.series;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.BackgroundStyle;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeChartSeries extends Series {
    List<Map.Entry<Number,Number>> data;
//    private List<List<Object>> data;

    public TimeChartSeries() {
        data= new ArrayList<>();
    }

    public void addSeries(Map.Entry<Number, Number> _data) {

        data.add(_data);
    }
    public  void SetSeries(String _type, Boolean _smooth,String _color) {
        type = _type;
        smooth = _smooth;
        color = _color;
    }
//      public void setSeries(List<List<Object>> _data, String _type, Boolean _smooth, String _color, Boolean _showBackground, BackgroundStyle _backgroundStyle) {
//        data = _data;
//        color = _color;
//        type = _type;
//        smooth = _smooth;
//        showBackground=_showBackground;
//        backgroundStyle=_backgroundStyle;
//    }

    public List<Map.Entry<Number, Number>> getData() {
        return data;
    }

    public void setData(List<Map.Entry<Number, Number>> data) {
        this.data = data;
    }
}

