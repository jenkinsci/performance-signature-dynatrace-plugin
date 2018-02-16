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

package de.tsystems.mms.apm.performancesignature.ui.util;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import hudson.FilePath;
import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.analysis.util.PluginLogger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

public final class PerfSigUIUtils {
    private PerfSigUIUtils() {
    }

    public static BigDecimal round(final Double d, final int scale) {
        if (d == null) {
            return BigDecimal.ZERO;
        }
        try {
            return BigDecimal.valueOf(d).setScale(d % 1 == 0 ? 0 : scale, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException ex) {
            if (Double.isInfinite(d)) {
                return BigDecimal.valueOf(d);
            } else {
                return BigDecimal.ZERO;
            }
        }
    }

    public static FilePath getReportDirectory(final Run<?, ?> run) throws IOException, InterruptedException {
        FilePath reportDirectory = new FilePath(new File(run.getRootDir(), "performance-signature"));
        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs();
        }
        return reportDirectory;
    }

    public static List<FilePath> getDownloadFiles(final String testCase, final Run<?, ?> build) throws IOException, InterruptedException {
        FilePath filePath = PerfSigUIUtils.getReportDirectory(build);
        return filePath.list(new RegexFileFilter(testCase));
    }

    public static PluginLogger createLogger(final PrintStream printStream) {
        return new PluginLogger(printStream, "[PERFSIG] ");
    }

    public static String removeExtension(final String fileName) {
        return FilenameUtils.removeExtension(fileName);
    }

    /*
    Change this in case of new Dashboardstuff
    Used for rewriting diagram titles from Time to WebService-Time etc.
    */
    public static String generateTitle(final String measure, final String chartDashlet, String aggregation) {
        String chartDashletName;
        if (StringUtils.deleteWhitespace(measure).equalsIgnoreCase(StringUtils.deleteWhitespace(chartDashlet))) {
            chartDashletName = chartDashlet;
        } else {
            chartDashletName = chartDashlet + " - " + measure;
        }
        return chartDashletName + " (" + aggregation + ")";
    }

    public static List<ChartDashlet> sortChartDashletList(final List<ChartDashlet> list) {
        Collections.sort(list);
        return list;
    }

    /**
     * Escape the given string to be used as URL query value.
     *
     * @param str String to be escaped
     * @return Escaped string
     */
    public static String encodeString(final String str) {
        try {
            return URLEncoder.encode(str, CharEncoding.UTF_8).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static String getDurationString(final float seconds) {
        int minutes = (int) ((seconds % 3600) / 60);
        float rest = seconds % 60;
        return minutes + " min " + (int) rest + " s";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    public static String toIndentedString(Object o) {
        return String.valueOf(o).replace("\n", "\n    ");
    }

    public static void handleIncidents(final Run<?, ?> run, final List<Alert> incidents, final PluginLogger logger, final int nonFunctionalFailure) {
        int numWarning = 0;
        int numSevere = 0;
        if (incidents != null && !incidents.isEmpty()) {
            logger.log(Messages.PerfSigUIUtils_FollowingIncidents());
            for (Alert incident : incidents) {
                logger.logLines(printIncident(incident));
                switch (incident.getSeverity()) {
                    case SEVERE:
                        numSevere++;
                        break;
                    case WARNING:
                        numWarning++;
                        break;
                    default:
                        break;
                }
            }

            switch (nonFunctionalFailure) {
                case 1:
                    if (numSevere > 0) {
                        logger.log(Messages.PerfSigUIUtils_BuildsStatusSevereIncidentsFailed());
                        run.setResult(Result.FAILURE);
                    }
                    break;
                case 2:
                    if (numSevere > 0 || numWarning > 0) {
                        logger.log(Messages.PerfSigUIUtils_BuildsStatusWarningIncidentsFailed());
                        run.setResult(Result.FAILURE);
                    }
                    break;
                case 3:
                    if (numSevere > 0) {
                        logger.log(Messages.PerfSigUIUtils_BuildsStatusSevereIncidentsUnstable());
                        run.setResult(Result.UNSTABLE);
                    }
                    break;
                case 4:
                    if (numSevere > 0 || numWarning > 0) {
                        logger.log(Messages.PerfSigUIUtils_BuildsStatusWarningIncidentsUnstable());
                        run.setResult(Result.UNSTABLE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static String printIncident(Alert incident) {
        StringBuilder sb = new StringBuilder();
        sb.append("[PERFSIG] ").append(incident.getSeverity()).append(" incident: \n");
        sb.append("[PERFSIG]    rule: ").append(toIndentedString(incident.getRule())).append("\n");
        sb.append("[PERFSIG]    message: ").append(toIndentedString(incident.getMessage())).append("\n");
        sb.append("[PERFSIG]    description: ").append(toIndentedString(incident.getDescription())).append("\n");
        return sb.toString();
    }

    public static boolean checkNotNullOrEmpty(final String string) {
        return StringUtils.isNotBlank(string);
    }

    public static boolean checkNotEmptyAndIsNumber(final String number) {
        return StringUtils.isNotBlank(number) && NumberUtils.isNumber(number);
    }
}
