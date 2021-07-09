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

package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.AggregationTypeEnum;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.TimeseriesDataPointQueryResult;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.TestUtils;
import hudson.AbortException;
import hudson.util.ListBoxModel;
import org.apache.commons.collections.MapUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.Assert.assertNotNull;

public class DynatraceReportStepExecutionTest {

    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();
    private static ListBoxModel dynatraceConfigurations;
    private final DynatraceServerConnection connection;

    public DynatraceReportStepExecutionTest() throws AbortException, RESTErrorException {
        connection = DynatraceUtils.createDynatraceServerConnection(dynatraceConfigurations.get(0).name, false);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDynatraceConfigurations();
    }

    /*@Test
    public void testExceptions() {
       connection.getTimeseriesData("com2.dynatrace.builtin:service.responsetime",
                Instant.now().minus(2, HOURS).toEpochMilli(), Instant.now().toEpochMilli(), AggregationTypeEnum.AVG, null, null);
        connection.createEvent(new EventPushMessage(EventTypeEnum.AVAILABILITY_EVENT, new PushEventAttachRules()));
        exception.expect(CommandExecutionException.class);
    }*/

    @Test
    public void testTimeseriesApi() {
        //final String timeseriesId = "com.dynatrace.builtin:host.mem.used";
        final String timeseriesId = "com.dynatrace.builtin:service.responsetime";

        TimeseriesDataPointQueryResult response = connection.getTimeseriesData(timeseriesId,
                Instant.now().minus(2, HOURS).toEpochMilli(), Instant.now().toEpochMilli(), AggregationTypeEnum.AVG, null, null);
        assertNotNull(response);
        MapUtils.debugPrint(System.out, "myMap", response.getDataPoints());

        Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> map = new LinkedHashMap<>();
        map.put(AggregationTypeEnum.AVG, response);
        DynatraceReportStepExecution.convertUnitOfDataPoints(map);

        MapUtils.debugPrint(System.out, "myMap2", map.get(AggregationTypeEnum.AVG).getDataPoints());
    }
}
