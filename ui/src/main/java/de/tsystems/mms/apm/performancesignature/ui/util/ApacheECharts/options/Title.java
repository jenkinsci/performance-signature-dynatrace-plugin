package de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.options;

import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.styles.SubTextStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.ApacheECharts.styles.TextStyle;


public class Title {
        private String text;
        private String left;
        private String subtext;
        private TextStyle textStyle;
        private SubTextStyle subtextStyle;
        public Title() {
        }

        public String getText() {
                return text;
        }

        public void setText(String text) {
                text=text.replace(" - ", "\n");
                this.text = text;
        }

        public String getLeft() {
                return left;
        }

        public void setLeft(String left) {
                this.left = left;
        }

        public String getSubtext() {
                return subtext;
        }

        public void setSubtext(String subtext) {
                this.subtext = subtext;
        }

        public TextStyle getTextStyle() {
                return textStyle;
        }

        public void setTextStyle(TextStyle textStyle) {
                this.textStyle = textStyle;
        }

        public SubTextStyle getSubtextStyle() {
                return subtextStyle;
        }

        public void setSubtextStyle(SubTextStyle subtextStyle) {
                this.subtextStyle = subtextStyle;
        }
}

