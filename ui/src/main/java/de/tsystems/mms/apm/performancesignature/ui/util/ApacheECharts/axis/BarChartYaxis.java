package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.styles.NameTextStyle;

public class BarChartYaxis extends  yAxis{
    public void setYaxis(String _type, String _name, String _nameLocation, int _nameGap, NameTextStyle _nameTextStyle) {
        type=_type;
        name=_name;
        nameLocation=_nameLocation;
        nameGap=_nameGap;
        nameTextStyle=_nameTextStyle;
    }
}
