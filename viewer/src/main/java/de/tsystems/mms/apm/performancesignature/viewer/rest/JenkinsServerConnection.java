/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
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

package de.tsystems.mms.apm.performancesignature.viewer.rest;

import com.google.common.base.Optional;
import com.google.gson.*;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.Job;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.viewer.model.CredJobPair;
import de.tsystems.mms.apm.performancesignature.viewer.model.CustomProxy;
import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import de.tsystems.mms.apm.performancesignature.viewer.rest.model.CustomJenkinsHttpClient;
import de.tsystems.mms.apm.performancesignature.viewer.rest.model.RootElement;
import hudson.FilePath;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.util.XStream2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class JenkinsServerConnection {
    private static final Logger LOGGER = Logger.getLogger(JenkinsServerConnection.class.getName());
    private Job jenkinsJob;
    private JenkinsServer jenkinsServer;
    private final Gson gson;

    public JenkinsServerConnection(final String serverUrl, final CredJobPair pair, final boolean verifyCertificate,
                                   final CustomProxy customProxyServer) {
        try {
            URI uri = new URI(serverUrl);
            JenkinsHttpClient client;
            if (pair.getCredentials() == null) {
                client = new CustomJenkinsHttpClient(uri, null, null, verifyCertificate, customProxyServer);
            } else {
                client = new CustomJenkinsHttpClient(uri, pair.getCredentials().getUsername(), pair.getCredentials().getPassword().getPlainText(),
                        verifyCertificate, customProxyServer);
            }
            this.jenkinsServer = new JenkinsServer(client);
            String job = pair.getJenkinsJob();

            //handle folders wait for https://github.com/jenkinsci/java-client-api/pull/267
            if (job.contains("/")) {
                String[] parts = job.split("/");
                Job folderJob = jenkinsServer.getJob(parts[0]);
                Optional<FolderJob> folder = jenkinsServer.getFolderJob(folderJob);
                if (folder.isPresent()) {
                    this.jenkinsJob = folder.get().getJob(parts[1]);
                } else {
                    throw new CommandExecutionException("the given folder/job name does not match");
                }
            } else {
                this.jenkinsJob = jenkinsServer.getJob(pair.getJenkinsJob());
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }

        GsonBuilder builder = new GsonBuilder();
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        gson = builder.create();
    }

    public JenkinsServerConnection(final JenkinsServerConfiguration config, final CredJobPair pair) {
        this(config.getServerUrl(), pair, config.isVerifyCertificate(), config.getCustomProxy());
    }

    public List<DashboardReport> getMeasureDataFromJSON(int buildNumber) {
        try {
            URL url = new URL(getJenkinsJob().getUrl() + buildNumber + "/performance-signature/api/json?depth=10");
            String json = getJenkinsJob().getClient().get(url.toString());

            RootElement rootElement = gson.fromJson(json, RootElement.class);
            return rootElement.getDashboardReports();
        } catch (IOException | JsonSyntaxException e) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(e) + "could not retrieve records from remote Jenkins: ", e);
        }
    }

    public boolean validateConnection() {
        try {
            return jenkinsServer.isRunning() && getJenkinsJob() != null;
        } catch (CommandExecutionException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    private List getReportList(final ReportType type, final int buildNumber) throws IOException {
        URL url = new URL(getJenkinsJob().getUrl() + buildNumber + "/performance-signature/get" + type + "ReportList");
        String xml = getJenkinsJob().getClient().get(url.toString());
        XStream2 xStream = new XStream2();
        List obj = (List) xStream.fromXML(xml);
        return obj != null ? obj : Collections.emptyList();
    }

    public boolean downloadPDFReports(final int buildNumber, final FilePath dir, final PluginLogger logger) {
        boolean result = true;
        try {
            for (ReportType reportType : ReportType.values()) {
                List reportlist = getReportList(reportType, buildNumber);
                for (Object report : reportlist) {
                    URL url = new URL(getJenkinsJob().getUrl() + buildNumber + "/performance-signature/get" + reportType + "Report?number="
                            + reportlist.indexOf(report));
                    result &= downloadArtifact(new FilePath(dir, report + ".pdf"), url, logger);
                }
            }
            return result;
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading PDF Reports: " + e.getMessage(), e);
        }
    }

    public boolean downloadSession(final int buildNumber, final FilePath dir, final String testCase, final PluginLogger logger) {
        try {
            URL url = new URL(getJenkinsJob().getUrl() + "/" + buildNumber + "/performance-signature/getSession?testCase=" + testCase);
            String sessionFileName = getJenkinsJob().getName() + "_Build_" + buildNumber + "_" + testCase + ".dts";
            return downloadArtifact(new FilePath(dir, sessionFileName), url, logger);
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading sessions: " + e.getMessage(), e);
        }
    }

    private boolean downloadArtifact(final FilePath file, final URL url, final PluginLogger logger) {
        try {
            InputStream inputStream = getJenkinsJob().getClient().getFile(url.toURI());
            file.copyFrom(inputStream);
            return true;
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.log("Could not download artifact: " + FilenameUtils.getBaseName(url.toString()));
            return false;
        }
    }

    public Job getJenkinsJob() {
        return this.jenkinsJob;
    }

    public JenkinsServer getJenkinsServer() {
        return this.jenkinsServer;
    }

    public void triggerInputStep(final int buildNumber, final String triggerId) {
        try {
            String url = getJenkinsJob().getUrl() + buildNumber + "/input/" + triggerId + "/proceedEmpty";
            getJenkinsJob().getClient().post(url, true);
            getJenkinsJob().getClient().get("url");
        } catch (IOException e) {
            throw new CommandExecutionException("error triggering input step: " + e.getMessage(), e);
        }
    }

    private enum ReportType {
        Single, Comparison
    }
}
