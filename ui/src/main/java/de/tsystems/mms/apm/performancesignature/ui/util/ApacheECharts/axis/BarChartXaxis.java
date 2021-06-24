package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.AxisLabel;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.styles.NameTextStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.styles.TextStyle;

import java.util.List;

public class BarChartXaxis extends xAxis {
    private List<String> data;
    public void setXaxis(List<String> _data, String _type, String _name, String _nameLocation, int _nameGap, NameTextStyle _nameTextStyle, AxisLabel _axisLabel)
    {
        data=_data;
        type=_type;
        name=_name;
        nameLocation=_nameLocation;
        nameGap=_nameGap;
        nameTextStyle=_nameTextStyle;
        axisLabel=_axisLabel;
    }
}
