/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@ExportedBean
public class Measure extends MeasureBaseModel {

    @XmlElement(name = "measurement")
    private List<Measurement> measurements;
    @SerializedName("measure")
    @XmlAttribute(name = "measure")
    private String name;
    @XmlElement(name = "measure")
    private List<Measure> measures;
    @XmlAttribute
    private String color;
    @XmlAttribute
    private String aggregation;
    @XmlAttribute
    private String unit;

    public Measure(String name) {
        this.name = name;
    }

    public Measure() {
    }

    /**
     * Gets the value of the measurement property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the measurement property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMeasurement().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Measurement }
     */
    @Exported
    public List<Measurement> getMeasurements() {
        if (measurements == null) {
            measurements = new ArrayList<>();
        }
        return this.measurements;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    @Exported(name = "measure")
    public String getName() {
        return name;
    }

    public List<Measure> getMeasures() {
        if (measures == null) {
            measures = new ArrayList<>();
        }
        return measures;
    }

    /**
     * Ruft den Wert der color-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    @Exported
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Ruft den Wert der aggregation-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    @Exported
    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    /**
     * Ruft den Wert der unit-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    @Exported
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit(final String aggregation) {
        return "count".equalsIgnoreCase(aggregation) ? "num" : this.unit;
    }

    private boolean isPercentile() {
        return "percentiles".equalsIgnoreCase(this.aggregation);
    }

    public double getMetricValue() {
        return getMetricValue(this.getAggregation());
    }

    public BigDecimal getStrMetricValue() {
        return PerfSigUIUtils.round(getMetricValue());
    }
}
