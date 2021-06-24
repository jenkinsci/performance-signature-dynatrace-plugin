package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.series;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.BackgroundStyle;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BarTimeSeries extends Series  {
//    List<List<Object>> data;
    List<Map.Entry<Number,Number>> data;
//    public void setSeries(List<List<Object>> _data, String _type, String _color, Boolean _showBackground, BackgroundStyle _backgroundStyle) {
//        data = _data;
//        type = _type;
//        color = _color;
//        showBackground=_showBackground;
//    }
    public void addSeries(Map.Entry<Number, Number> _data) {

        data.add(_data);
    }
    public  void SetSeries(String _type, Boolean _smooth,String _color) {
        type = _type;
        smooth = _smooth;
        color = _color;
    }

    public List<Map.Entry<Number, Number>> getData() {
        return data;
    }

    public void setData(List<Map.Entry<Number, Number>> data) {
        this.data = data;
    }
}
