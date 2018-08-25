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

package de.tsystems.mms.apm.performancesignature.dynatrace.configuration;

import de.tsystems.mms.apm.performancesignature.ui.model.ClientLinkGenerator;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.DescriptorExtensionList;
import hudson.RelativePath;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class ConfigurationTestCase implements Describable<ConfigurationTestCase>, Serializable {
    private final String name;
    private final String xmlDashboard;
    private final List<Dashboard> singleDashboards;
    private final List<Dashboard> comparisonDashboards;
    private String clientDashboard;

    public ConfigurationTestCase(final String name, final List<Dashboard> singleDashboards,
                                 final List<Dashboard> comparisonDashboards, final String xmlDashboard) {
        this.name = StringUtils.deleteWhitespace(name);
        this.singleDashboards = singleDashboards;
        this.comparisonDashboards = comparisonDashboards;
        this.xmlDashboard = xmlDashboard;
    }

    public String getName() {
        ConfigurationTestCaseDescriptor.addTestCases(name);
        return name;
    }

    public String getXmlDashboard() {
        return xmlDashboard;
    }

    public String getClientDashboard() {
        return clientDashboard;
    }

    public void setClientDashboard(final String clientDashboard) {
        this.clientDashboard = StringUtils.isBlank(clientDashboard) ? ConfigurationTestCaseDescriptor.defaultClientDashboard : clientDashboard;
    }

    @Nonnull
    public List<Dashboard> getSingleDashboards() {
        if (singleDashboards == null) {
            return new ArrayList<>();
        }
        return singleDashboards;
    }

    @Nonnull
    public List<Dashboard> getComparisonDashboards() {
        if (comparisonDashboards == null) {
            return new ArrayList<>();
        }
        return comparisonDashboards;
    }

    public boolean validate() {
        return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(xmlDashboard);
    }

    public ConfigurationTestCaseDescriptor getDescriptor() {
        return (ConfigurationTestCaseDescriptor) Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public abstract static class ConfigurationTestCaseDescriptor extends Descriptor<ConfigurationTestCase> {
        public static final String defaultClientDashboard = ClientLinkGenerator.WEBSTART;
        private static final Set<String> testCases = new LinkedHashSet<>(); //avoid duplicates

        public static void addTestCases(final String testCase) {
            if (StringUtils.isNotBlank(testCase)) {
                testCases.add(testCase);
            }
        }

        public static DescriptorExtensionList<ConfigurationTestCase, Descriptor<ConfigurationTestCase>> all() {
            return Jenkins.getInstance().getDescriptorList(ConfigurationTestCase.class);
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillNameItems(@AncestorInPath Item item) {
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return new ListBoxModel();
            }

            final ListBoxModel out = new ListBoxModel();
            testCases.forEach(out::add);
            return out;
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillClientDashboardItems(@AncestorInPath Item item) {
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return new ListBoxModel();
            }

            ListBoxModel out = new ListBoxModel();
            out.add(ClientLinkGenerator.LOADTEST_OVERVIEW)
                    .add(ClientLinkGenerator.PUREPATH_OVERVIEW)
                    .add(ClientLinkGenerator.WEBSTART);
            return out;
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillXmlDashboardItems(@AncestorInPath Item item,
                                                    @RelativePath("..") @QueryParameter final String dynatraceProfile) {
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return new ListBoxModel();
            }
            return new Dashboard.DescriptorImpl().doFillDashboardItems(item, dynatraceProfile);
        }
    }
}
