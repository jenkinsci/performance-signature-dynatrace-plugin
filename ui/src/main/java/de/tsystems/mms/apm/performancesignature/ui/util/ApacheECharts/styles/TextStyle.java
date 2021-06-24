package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.styles;

import java.io.Serializable;

public class TextStyle  {
    private String fontStyle;
    public String fontWeight;
    public String fontFamily;
    public Integer fontSize;
    public String  overflow;
    public Integer width;

    public TextStyle() {
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getOverflow() {
        return overflow;
    }

    public void setOverflow(String overflow) {
        this.overflow = overflow;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "TextStyle{" +
                "fontStyle='" + fontStyle + '\'' +
                ", fontWeight='" + fontWeight + '\'' +
                ", fontFamily='" + fontFamily + '\'' +
                ", fontSize=" + fontSize +
                ", overflow='" + overflow + '\'' +
                ", width=" + width +
                '}';
    }
}
