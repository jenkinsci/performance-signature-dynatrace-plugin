package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.series;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.BackgroundStyle;

import java.io.Serializable;
import java.util.List;

public class BarSeries extends Series  {
    private List<Double> data;

    public void setSeries(List<Double> _data, String _type, Boolean _showBackground, BackgroundStyle _backgroundStyle) {
        data = _data;
        type = _type;
        showBackground=_showBackground;
        backgroundStyle=_backgroundStyle;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }
}
