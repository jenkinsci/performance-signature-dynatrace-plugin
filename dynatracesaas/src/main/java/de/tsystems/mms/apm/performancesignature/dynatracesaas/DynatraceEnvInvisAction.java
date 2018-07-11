/*
 * Copyright 2014 T-Systems Multimedia Solutions GmbH
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

package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import hudson.model.InvisibleAction;

import java.time.LocalDateTime;

class DynatraceEnvInvisAction extends InvisibleAction {
    private final String testCase;
    private final LocalDateTime timeframeStart;
    private LocalDateTime timeframeStop;

    DynatraceEnvInvisAction(final String testCase, final LocalDateTime timeframeStart) {
        this.timeframeStart = timeframeStart;
        this.testCase = testCase;
    }

    public String getTestCase() {
        return testCase;
    }

    public LocalDateTime getTimeframeStart() {
        return timeframeStart;
    }

    public LocalDateTime getTimeframeStop() {
        return timeframeStop;
    }

    void setTimeframeStop(LocalDateTime timeframeStop) {
        this.timeframeStop = timeframeStop;
    }
}
