package de.tsystems.mms.apm.performancesignature.dynatrace;

import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import static org.mockserver.model.HttpResponse.notFoundResponse;

@SuppressWarnings("unused")
public class MockServerResponses implements ExpectationResponseCallback {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        switch (httpRequest.getPath().getValue()) {
            case "/api/v2/server/version":
                return new HttpResponse().withBody("{\"result\":\"7.2.5.1022\"}").withStatusCode(200);
            case "/rest/management/server/license":
                return new HttpResponse().withBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><licenseinformation><licensedto>T-Systems International GmbH - Self-Service License 'Partner License AppMon 7 Pre-Prod'</licensedto><licensenumber>201902181026</licensenumber><licenseedition>Pre-Production Edition</licenseedition><usedvolumepercentage>0.0</usedvolumepercentage><nextvolumerenewaldate></nextvolumerenewaldate><nextvolumerenewaldate-iso></nextvolumerenewaldate-iso><validfrom>2019-02-17</validfrom><validfrom-iso>2019-02-17T01:00:00+01:00</validfrom-iso><expiredate>2019-03-23</expiredate><expiredate-iso>2019-03-23T12:58:00+01:00</expiredate-iso><currentuemtransactions>100000</currentuemtransactions><maximaluemtransactions>100000</maximaluemtransactions><licensedagents><agent name=\"Java\">5</agent><agent name=\"Message Broker\">0</agent><agent name=\"Browser\">2</agent><agent name=\"PHP\">5</agent><agent name=\"Web server\">5</agent><agent name=\"z/OS\">0</agent><agent name=\"Host Monitoring\">1</agent><agent name=\"Java (NoSQL)\">0</agent><agent name=\"Node.js\">5</agent><agent name=\"ADK\">1</agent><agent name=\"Database\">0</agent><agent name=\"Windows Instances\">5</agent><agent name=\"Web Server Developer\">0</agent></licensedagents></licenseinformation>").withStatusCode(200);
            case "/api/v2/profiles/easy%20Travel/session/recording/status":
                return new HttpResponse().withBody("{\"recording\": false}").withStatusCode(200);
            case "/api/v2/profiles/easy%20Travel/session/recording":
                return new HttpResponse().withHeader("Location", "https://wum192202:8021/api/v2/sessions/easy+Travel%252F20190225093834_0.t0.session").withStatusCode(201);
            case "/api/v2/profiles/easy%20Travel/testruns":
                return new HttpResponse().withBody("{\n"
                        + "  \"id\": \"fb8de4e2-0c94-45d4-b386-a590f9747e38\",\n"
                        + "  \"category\": \"unit\",\n"
                        + "  \"versionBuild\": \"string\",\n"
                        + "  \"versionMajor\": \"string\",\n"
                        + "  \"versionMilestone\": \"string\",\n"
                        + "  \"versionMinor\": \"string\",\n"
                        + "  \"versionRevision\": \"string\",\n"
                        + "  \"platform\": \"string\",\n"
                        + "  \"startTime\": \"2019-02-25T10:24:48.520+01:00\",\n"
                        + "  \"systemProfile\": \"easy Travel\",\n"
                        + "  \"marker\": \"string\",\n"
                        + "  \"href\": \"https://localhost:8021/api/v2/profiles/easy%20Travel/testruns/fb8de4e2-0c94-45d4-b386-a590f9747e38\",\n"
                        + "  \"creationMode\": \"MANUAL\",\n"
                        + "  \"numDegraded\": 0,\n"
                        + "  \"numFailed\": 0,\n"
                        + "  \"numImproved\": 0,\n"
                        + "  \"numInvalidated\": 0,\n"
                        + "  \"numPassed\": 0,\n"
                        + "  \"numVolatile\": 0,\n"
                        + "  \"finished\": false,\n"
                        + "  \"includedMetrics\": [\n"
                        + "    {\n"
                        + "      \"group\": \"string\",\n"
                        + "      \"metric\": \"string\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}").withStatusCode(201);
            default:
                return notFoundResponse();
        }
    }
}
