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

import de.tsystems.mms.apm.performancesignature.ui.PerfSigTestAction;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.TestAction;
import hudson.tasks.junit.TestObject;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestResult;

import java.util.Collections;
import java.util.List;

public class PerfSigTestData extends TestResultAction.Data {
    private final List<TestRun> testRuns;

    public PerfSigTestData(final List<TestRun> testRuns) {
        this.testRuns = testRuns;
    }

    public List<TestRun> getTestRuns() {
        return testRuns == null ? Collections.emptyList() : testRuns;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<? extends TestAction> getTestAction(final TestObject testObject) {
        if (testObject instanceof CaseResult || testObject instanceof TestResult) {
            return Collections.singletonList(new PerfSigTestAction((hudson.tasks.test.TestObject) testObject, this));
        } else {
            return Collections.emptyList();
        }
    }
}
