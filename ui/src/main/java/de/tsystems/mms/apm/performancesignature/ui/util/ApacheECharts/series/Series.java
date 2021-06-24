package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.series;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options.BackgroundStyle;

import java.io.Serializable;
import java.util.List;

public abstract class Series {
    protected Boolean showBackground;
    protected String type;
    protected String color;
    protected Boolean smooth;
    BackgroundStyle backgroundStyle;

    public Boolean getShowBackground() {
        return showBackground;
    }

    public void setShowBackground(Boolean showBackground) {
        this.showBackground = showBackground;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getSmooth() {
        return smooth;
    }

    public void setSmooth(Boolean smooth) {
        this.smooth = smooth;
    }

    public BackgroundStyle getBackgroundStyle() {
        return backgroundStyle;
    }

    public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }
}
