package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.axis;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.AxisLabel;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.styles.NameTextStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.styles.TextStyle;

public abstract class xAxis {
    protected String type;
    protected String name;
    protected String nameLocation;
    protected int nameGap;
    protected AxisLabel axisLabel;
    protected TextStyle textStyle;
    protected NameTextStyle nameTextStyle;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }

    public int getNameGap() {
        return nameGap;
    }

    public void setNameGap(int nameGap) {
        this.nameGap = nameGap;
    }

    public AxisLabel getAxisLabel() {
        return axisLabel;
    }

    public void setAxisLabel(AxisLabel axisLabel) {
        this.axisLabel = axisLabel;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public NameTextStyle getNameTextStyle() {
        return nameTextStyle;
    }

    public void setNameTextStyle(NameTextStyle nameTextStyle) {
        this.nameTextStyle = nameTextStyle;
    }
}
