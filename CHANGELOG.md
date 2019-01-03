Changelog
====
### Version 3.0.2 (Jan 03, 2019)
* fix CVE-2018-1000850 and CVE-2018-1000844
* drop analysis-core dependency
* fix NPE in round method
* whitelist some methods in PerfSigEnvInvisAction class

### Version 3.0.1 (Sep 14, 2018)
* fixed release problems

### Version 3.0.0 (Sep 13, 2018)
* new module: Performance Signature for Dynatrace Saas/Managed
* Requires Jenkins Core 2.60.3 or newer
* Requires Java 8
* complete overhaul of the http client
* removed custom proxy option
* security hardening of all web methods
* group up all incidents by severity and message
* be more verbose about network related exception
* changed argument values for parameter 'nonFunctionalFailure'

### Version 2.5.8 (Jul 04, 2018)
* complete rewrite of the Performance Signature viewer module
* Dev: use yarn instead of npm
* Dev: update parent pom version to 3.17

### Version 2.5.7 (May 17, 2018)
* major Bugfix: don't use a static instance of ApiClient
* Dev: code cleanup
* Dev: update parent pom version to 3.8

### Version 2.5.6 (Mar, 26, 2018)
* Bugfix: Grid arrangement gets mixed up
* Bugfix: delete only the previous session
* mark pipeline step dependency as optional
* Dev: update parent pom version to 3.6

### Version 2.5.2 (Jan 17, 2018)
* Bugfix: Viewer configuration was not saved
* Bugfix: removed minimum dashboard requirement from jelly
* Feature: added option to delete sessions after reporting and session export
* Dev: update parent pom version to 3.2
* Dev: update gridster JS lib to latest version

### Version 2.5.1 (Dec 22, 2017)
* Bugfix: empty chart configuration caused by missing alias

### Version 2.5.0 (Dec 21, 2017)
* only compatible with Dynatrace AppMon 7.0 and higher
* added "Create Deployment" Pipeline Step
* all log output is now prefixed by "[PERFSIG]"
* complete rewrite of rest interface by using a generated java client 
* reordered Session Recording and TestRun creation/finish
* reanalyse Session Recording option is not available anymore
* simplified test data persistence
* actual test run id can be accessed via "DYNATRACE_TESTRUN_ID"
* confidential strings can now be removed (default: true)
* read timeout can be configured (default: 300s)
* aligned wording of plugin names
* use JAXB for xml parsing
* show incidents within the Performance Signature Build report
* huge internal code clean up

### Version 2.4.4 (Sep 13, 2017)
* replaced protocol, host and port with REST endpoint URL in global configuration
* fix a visual bug by using Pipeline: Stage View Plugin and Performance Signature Plugin in a pipeline job
* internal code clean up

### Version 2.4.3 (Jun 07, 2017)
* Java 7 is now a minimum requirement
* updated JS dependencies
* sort all ListBoxes
* fix PerfSigViewer remote build step

### Version 2.4.2 (Mar 28, 2017)
* updated dependencies
* avoid duplicated charts by using dynamic measures (e.g. application splitting)
* added a random parameter, so charts don't get cached

### Version 2.4.1 (Jan 26, 2017)
* refactored log messages
* added aggregation to chart title
* fixed empty aggregation select box
* fill chart's custom name while selecting measure
* fixed missing incident handling with >= Dynatrace 6.5
* added UnitTests
* a lot code cleanup
* refactored charting
* fixed use wrong session name with continuous session recording turned on
* fixed XML parsing of agent names
* handle build cancellation in viewer module

### Version 2.3.3 (Nov 16, 2016)
* fixed regression from 2.3.2: grid configuration could not be loaded with more than one testcase

### Version 2.3.2 (Nov 11, 2016)
* use GlobalConfiguration ExtensionPoint for Viewer module
* updated Gridster JS library
* Chart configuration related fixes & improvements
* added logger to monitor configuration read & write

### Version 2.3.1 (Oct 12, 2016)
* decreased log level, use LogRecorder de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection and LogLevel fine
* use GlobalConfiguration ExtensionPoint
* fix empty session names with continuous session recording turned on

### Version 2.3.0 (Sep 23, 2016)
* Code optimization
* sort select boxes in performance signature overview
* complete dashboard configuration rewrite
* no JSON configuration files anymore, everthing is saved in gridconfig.xml
* smaller bug fixes

### Version 2.2.3 (Sep 02, 2016)
* Code optimization
* introduced REST Interface: http://<JENKINS>/job/<JOB_NAME>/<BUILD#>/performance-signature/api/xml?depth=10
* new plugin dependency: structs plugin >= 1.2 (gets installed automatically)
* improved session recording

### Version 2.2.2 (Aug 17, 2016)
* use symbols in order to simplify pipeline usage
* fixed JUnit test data not visible

### Version 2.2.1 (Aug 02, 2016)
* fix NPE in IncidentViolation
* Regression from 2.1.3 PDFs should be downloaded properly
* fix color decoding

### Version 2.2.0 (Jul 22, 2016)
* parse incident start/end date properly
* fix datatable exceptions (workaround)
* splitted UI from REST API
* removed percentile aggregation (session based percentiles are not working)
* code cleanup

### Version 2.1.3 (Jun 27, 2016)
* using Credentials > 2.1.0
* require Jenkins > 1.609.1
* continuous session recording is supported now
* fix renamed or deleted measures
* expose Performance Signature data to rest api
* code cleanup

### Version 2.1.2 (Jun 14, 2016)
* fix Dashboard configuration with aggregation value
* fix empty session file list

### Version 2.1.1 (Jun 02, 2016)
* several cosmetic changes
* fix java 6 missing TLS 1.2
* updated DataTables
* fixed unit aggregation

### Version 2.1.0 (May 18, 2016)
* added option to change aggregation value
* simplified single/comparison report & session URLs
* added link to Dynatrace client from detail view
* added Jenkins 2.0 compatibility
* some string fixes
* increase reanalyzeSessionTimeout to 5 minutes

### Version 2.0 (Feb 23, 2016) (initial release)
* changed pluginID to 'performance-signature-dynatrace'
* compatibility with Pipeline
* moved server configuration to global configuration
* lots of bug fixes
* bumped versions of jQuery and other JS libraries
* added migrationsscript
