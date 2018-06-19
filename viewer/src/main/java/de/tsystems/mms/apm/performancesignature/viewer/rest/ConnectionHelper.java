package de.tsystems.mms.apm.performancesignature.viewer.rest;

import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.AbortException;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.*;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.auth2.Auth2;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.exceptions.ForbiddenException;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.exceptions.UnauthorizedException;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.pipeline.Handle;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.jenkinsci.plugins.ParameterizedRemoteTrigger.utils.StringTools.NL;

public class ConnectionHelper {
    private static final int DEFAULT_POLLINTERVALL = 10;
    private static final int connectionRetryLimit = 5;

    private final Handle handle;

    public ConnectionHelper(final Handle handle) {
        this.handle = handle;
    }

    public Handle getHandle() {
        return handle;
    }

    /**
     * Convenience function to mark the build as failed. It's intended to only be called from this.perform().
     *
     * @param e      exception that caused the build to fail.
     * @param logger build listener.
     * @throws IOException if the build fails and <code>shouldNotFailBuild</code> is not set.
     */
    protected void failBuild(final Exception e, final PrintStream logger) throws IOException {
        StringBuilder msg = new StringBuilder();
        if (e instanceof InterruptedException) {
            Thread current = Thread.currentThread();
            msg.append(String.format("[Thread: %s/%s]: ", current.getId(), current.getName()));
        }
        msg.append(String.format("Remote build failed with '%s' for the following reason: '%s'.%s",
                e.getClass().getSimpleName(), e.getMessage(), ""));
        msg.append(NL).append(ExceptionUtils.getFullStackTrace(e));
        if (logger != null) logger.println("ERROR: " + msg.toString());
        throw new AbortException(e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    private JenkinsCrumb getCrumb(final BuildContext context) throws IOException {
        String address = context.effectiveRemoteServer.getAddress();
        if (address == null) {
            throw new AbortException("The remote server address can not be empty, or it must be overridden on the job configuration.");
        }
        URL crumbProviderUrl;
        try {
            String xpathValue = URLEncoder.encode("concat(//crumbRequestField,\":\",//crumb)", "UTF-8");
            crumbProviderUrl = new URL(address.concat("/crumbIssuer/api/xml?xpath=").concat(xpathValue));
            HttpURLConnection connection = getAuthorizedConnection(context, crumbProviderUrl);
            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                throw new UnauthorizedException(crumbProviderUrl);
            } else if (responseCode == 403) {
                throw new ForbiddenException(crumbProviderUrl);
            } else if (responseCode == 404) {
                context.logger.println("CSRF protection is disabled on the remote server.");
                return new JenkinsCrumb();
            } else if (responseCode == 200) {
                context.logger.println("CSRF protection is enabled on the remote server.");
                String response = new String(readInputStream(connection));
                String[] split = response.split(":");
                return new JenkinsCrumb(split[0], split[1]);
            } else {
                throw new RuntimeException(String.format("Unexpected response. Response code: %s. Response message: %s", responseCode, connection.getResponseMessage()));
            }
        } catch (FileNotFoundException e) {
            context.logger.println("CSRF protection is disabled on the remote server.");
            return new JenkinsCrumb();
        }
    }

    private HttpURLConnection getAuthorizedConnection(final BuildContext context, final URL url) throws IOException {
        URLConnection connection = ProxyConfiguration.open(url);
        RemoteJenkinsServer remoteJenkinsServer = findRemoteHost(url.getHost());
        if (remoteJenkinsServer != null) {
            Auth2 serverAuth = remoteJenkinsServer.getAuth2();
            if (serverAuth != null) {
                //Set Authorization Header configured globally for remoteServer
                serverAuth.setAuthorizationHeader(connection, context);
            }
        }
        return (HttpURLConnection) connection;
    }

    public String getStringFromUrl(final URL url, final BuildContext context) throws IOException {
        return new String(sendHTTPCall(url, "GET", context));
    }

    public InputStream getInputStreamFromUrl(final URL url, final BuildContext context) throws IOException {
        return new ByteArrayInputStream(sendHTTPCall(url, "GET", context));
    }

    public void postToUrl(final URL url, final BuildContext context) throws IOException {
        sendHTTPCall(url, "POST", context);
    }

    /**
     * Same as sendHTTPCall, but keeps track of the number of failed connection attempts (aka: the number of times this
     * method has been called).
     * In the case of a failed connection, the method calls it self recursively and increments the number of attempts.
     *
     * @param url         the URL that needs to be called.
     * @param requestType the type of request (GET, POST, etc).
     * @param context     the context of this Builder/BuildStep.
     * @return {@link ConnectionResponse}
     * the response to the HTTP request.
     * @throws IOException if there is an error identifying the remote host, or
     *                     if there is an error setting the authorization header, or
     *                     if the request fails due to an unknown host or unauthorized credentials, or
     *                     if the request fails due to another reason and the number of attempts is exceeded.
     */
    private byte[] sendHTTPCall(final URL url, final String requestType, final BuildContext context)
            throws IOException {

        byte[] response = null;
        Map<String, List<String>> responseHeader = null;
        int responseCode = 0;
        int numberOfAttempts = 0;

        HttpURLConnection connection = getAuthorizedConnection(context, url);

        try {
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod(requestType);
            addCrumbToConnection(connection, context);
            // wait up to 5 seconds for the connection to be open
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            responseHeader = connection.getHeaderFields();
            responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                throw new UnauthorizedException(url);
            } else if (responseCode == 403) {
                throw new ForbiddenException(url);
            } else {
                response = readInputStream(connection);
            }

        } catch (UnknownHostException | UnauthorizedException | ForbiddenException e) {
            this.failBuild(e, context.logger);
        } catch (IOException e) {
            //E.g. "HTTP/1.1 403 No valid crumb was included in the request"
            List<String> hints = responseHeader != null ? responseHeader.get(null) : null;
            String hintsString = (hints != null && hints.size() > 0) ? " - " + hints.toString() : "";

            context.logger.println(e.getMessage() + hintsString);
            //If we have connectionRetryLimit set to > 0 then retry that many times.
            if (numberOfAttempts <= connectionRetryLimit) {
                context.logger.println(String.format(
                        "Connection to remote server failed %s, waiting for to retry - %s seconds until next attempt. URL: %s",
                        (responseCode == 0 ? "" : "[" + responseCode + "]"), DEFAULT_POLLINTERVALL, url));
                e.printStackTrace();

                // Sleep for 'pollInterval' seconds.
                // Sleep takes miliseconds so need to convert this.pollInterval to milisecopnds (x 1000)
                try {
                    // Could do with a better way of sleeping...
                    Thread.sleep(DEFAULT_POLLINTERVALL * 1000);
                } catch (InterruptedException ex) {
                    this.failBuild(ex, context.logger);
                }

                context.logger.println("Retry attempt #" + numberOfAttempts + " out of " + connectionRetryLimit);
                numberOfAttempts++;
                response = sendHTTPCall(url, requestType, context);
            } else if (numberOfAttempts > connectionRetryLimit) {
                //reached the maximum number of retries, time to fail
                this.failBuild(new Exception("Max number of connection retries have been exceeded."), context.logger);
            } else {
                //something failed with the connection and we retried the max amount of times... so throw an exception to mark the build as failed.
                this.failBuild(e, context.logger);
            }

        } finally {
            // always make sure we close the connection
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    /**
     * For POST requests a crumb is needed. This methods gets a crumb and sets it in the header.
     * https://wiki.jenkins.io/display/JENKINS/Remote+access+API#RemoteaccessAPI-CSRFProtection
     *
     * @param connection
     * @param context
     * @throws IOException
     */
    private void addCrumbToConnection(final HttpURLConnection connection, final BuildContext context) throws IOException {
        String method = connection.getRequestMethod();
        if (method != null && method.equalsIgnoreCase("POST")) {
            JenkinsCrumb crumb = getCrumb(context);
            if (crumb.isEnabledOnRemote()) {
                connection.setRequestProperty(crumb.getHeaderId(), crumb.getCrumbValue());
            }
        }
    }

    private byte[] readInputStream(final HttpURLConnection connection) throws IOException {
        InputStream is;
        try {
            is = connection.getInputStream();
        } catch (FileNotFoundException e) {
            // In case of a e.g. 404 status
            is = connection.getErrorStream();
        }
        return IOUtils.toByteArray(is);
    }

    /**
     * Lookup up the globally configured Remote Jenkins Server based on display name
     *
     * @param serverUrl Name of the configuration you are looking for
     * @return A deep-copy of the RemoteJenkinsServer object configured globally
     */
    @Nullable
    @CheckForNull
    private RemoteJenkinsServer findRemoteHost(final String serverUrl) {
        if (isEmpty(serverUrl)) return null;
        RemoteBuildConfiguration.DescriptorImpl descriptor = (RemoteBuildConfiguration.DescriptorImpl)
                Jenkins.getActiveInstance().getDescriptorOrDie(RemoteBuildConfiguration.class);
        for (RemoteJenkinsServer host : descriptor.getRemoteSites()) {
            String server = PerfSigUIUtils.getHostFromUrl(serverUrl);
            String hostname = PerfSigUIUtils.getHostFromUrl(host.getAddress());
            if (server != null && server.equals(hostname)) {
                return host;
            }
        }
        return null;
    }
}
