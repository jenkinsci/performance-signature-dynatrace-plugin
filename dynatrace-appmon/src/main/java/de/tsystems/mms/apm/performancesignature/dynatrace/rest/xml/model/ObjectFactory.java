package de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the generated package.
 * <p>An ObjectFactory allows you to programatically construct new instances of the Java representation
 * for XML content. The Java representation of XML content can consist of schema derived interfaces
 * and classes representing the binding of schema type definitions, element declarations and model
 * groups.  Factory methods for each of these are provided in this class.
 */
@SuppressWarnings("unused")
@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    public Agent createAgent() {
        return new Agent();
    }

    public AgentList createAgentList() {
        return new AgentList();
    }

    public Dashboard createDashboard() {
        return new Dashboard();
    }

    public DashboardList createDashboardList() {
        return new DashboardList();
    }

    public DashboardReport createDashboardReport() {
        return new DashboardReport();
    }

    public LicenseInformation createLicenseInformation() {
        return new LicenseInformation();
    }

    public XmlResult createXmlResult() {
        return new XmlResult();
    }
}
