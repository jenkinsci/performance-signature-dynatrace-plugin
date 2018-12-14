/*
 * Copyright (c) 2008-2011 Dr. Ullrich Hafner
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package de.tsystems.mms.apm.performancesignature.ui.util;

import org.apache.commons.lang.StringUtils;

import java.io.PrintStream;

/**
 * A simple logger that prefixes each message with the plug-in name.
 *
 * @author Ulli Hafner
 */
public class PluginLogger {
    /**
     * The plug-in name.
     */
    private final String pluginName;
    /**
     * The actual print stream to log to.
     */
    private PrintStream logger;

    /**
     * Creates a new instance of {@link PluginLogger}.
     *
     * @param logger     the actual print stream to log to
     * @param pluginName the plug-in name
     */
    public PluginLogger(final PrintStream logger, final String pluginName) {
        this.logger = logger;
        this.pluginName = pluginName;
    }

    /**
     * Creates a new instance of {@link PluginLogger}. Note that the logger
     * needs to be set afterwards to avoid throwing a {@link NullPointerException}.
     *
     * @param pluginName the plug-in name
     */
    // CHECKSTYLE:CONSTANTS-OFF
    protected PluginLogger(final String pluginName) {
        if (pluginName.contains("[")) {
            this.pluginName = pluginName;
        } else {
            this.pluginName = "[" + pluginName + "] ";
        }
    }
    // CHECKSTYLE:CONSTANTS-ON

    /**
     * Sets the logger to the specified value.
     *
     * @param logger the value to set
     */
    protected void setLogger(final PrintStream logger) {
        this.logger = logger;
    }

    /**
     * Logs the specified message.
     *
     * @param message the message
     */
    public void log(final String message) {
        logger.println(StringUtils.defaultString(pluginName) + message);
    }


    /**
     * Logs the specified throwable.
     *
     * @param throwable the throwable
     */
    public void log(final Throwable throwable) {
        logger.println(StringUtils.defaultString(pluginName) + throwable.getMessage());
    }

    /**
     * Logs the stack trace of the throwable.
     *
     * @param throwable the throwable
     */
    public void printStackTrace(final Throwable throwable) {
        throwable.printStackTrace(logger);
    }

    /**
     * Logs several lines that already contain a prefix.
     *
     * @param lines the lines to log
     */
    public void logLines(final String lines) {
        logger.print(lines);
    }
}
