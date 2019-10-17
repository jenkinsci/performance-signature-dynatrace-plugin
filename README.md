# Performance Signature with Dynatrace (AppMon) for Jenkins
[![Build Status](https://ci.jenkins.io/job/Plugins/job/performance-signature-dynatrace-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/performance-signature-dynatrace-plugin/job/master/)
[![Sonarqube](https://sonarcloud.io/api/project_badges/measure?project=de.tsystems.mms.apm%3Aperformance-signature-parent-pom&metric=security_rating)](https://sonarcloud.io/dashboard?id=de.tsystems.mms.apm%3Aperformance-signature-parent-pom)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=jenkinsci/performance-signature-dynatrace-plugin)](https://dependabot.com)

Smoking latest software products are created with the help of flexible and agile systems in a continuous integration (CI) environment. Such environments include a CI-Server like Jenkins.
 The Performance Signature collects performance values during a build and evaluates and compares the result with previous builds and non-functional requirements.
 Several software tests can be run and evaluated automatically, so that the most important key performance indicators (KPI) can be summarized and be available for all project participants very quickly.

### Get in touch with Application Performance Experts

![T-Systems](https://www.t-systems-mms.com/typo3conf/ext/mkmms/Resources/Public/img/logos/logo.png)

**T-Systems Multimedia Solutions GmbH**

Send a [mail](mailto:performance@t-systems-mms.com) to the Application Performance Team

### Awards
* Winner of the Dynatrace Solution Innovation Award EMEA 2016
* Winner of the Dynatrace EMEA API Challenge 2018

---
### Table of Contents

<!-- toc -->

- [Introduction](#introduction)
- [Installation](#installation)
  * [Using Jenkins Update Center](#using-jenkins-update-center)
  * [Manual Installation](#manual-installation)
- [Problems? Questions? Suggestions?](#problems-questions-suggestions)
- [Additional Resources](#additional-resources)
  * [Dynatrace AppMon Documentation](#dynatrace-appmon-documentation)
  * [Dynatrace SaaS/Managed Documentation](#dynatrace-saasmanaged-documentation)
  * [Links](#links)

<!-- tocstop -->

## Introduction

The Performance Signature evaluates aggregated load and performance data from Dynatrace after each build.
We are currently supporting three data sources:
* Dynatrace AppMon
* Dynatrace SaaS/Manged
* Remote Jenkins Jobs with enabled Performance Signature

Each data source has it's own Jenkins plugin, but all Jenkins plugins depend on the Performance Signature: UI Plugin.
Find below the documentation of each component.

* **[Performance Signature: Dynatrace AppMon](dynatrace-appmon/README.md)**
* **[Performance Signature: Dynatrace SaaS/Manged](dynatrace/README.md)**
* **[Performance Signature: Dynatrace Viewer](viewer/README.md)**

## Installation
### Using Jenkins Update Center

The recommended way of installing the plugin is by using the Update Center (plugin directory). Navigate to `Manage Jenkins -> Manage Plugins` page and switch to the `Available` tab. Search for the keyword "Performance Signature:" and install the plugin.

### Manual Installation

This procedure is meant for developers, who want to install a locally built plugin version.

* build the plugin from source using `mvn package` command
* in Jenkins, go to the `Manage Jenkins -> Manage Plugins`
* switch to the `Advanced` tab
* upload the built plugin package from the `target/performance-signature-*.hpi` path to the `Upload Plugin` section
* restart Jenkins

## Problems? Questions? Suggestions?

* Post any problems, questions or suggestions to the Dynatrace Community's [Application Monitoring & UEM Forum](https://answers.dynatrace.com/spaces/146/index.html).
* Contact the Performance Experts at T-Systems: `performance@t-systems-mms.com`

## Additional Resources

### Dynatrace AppMon Documentation

- [Continuous Delivery & Test Automation](https://community.dynatrace.com/community/pages/viewpage.action?pageId=215161284)
- [Capture Performance Data from Tests](https://community.dynatrace.com/community/display/DOCDT63/Capture+Performance+Data+from+Tests)
- [Integrate Dynatrace in Continuous Integration Builds](https://community.dynatrace.com/community/display/DOCDT63/Integrate+Dynatrace+in+Continuous+Integration+Builds)

### Dynatrace SaaS/Managed Documentation

- https://www.dynatrace.com/support/help/

### Links

* [Dynatrace Plugin Page](https://community.dynatrace.com/community/display/DL/Performance+Signature+Plugin)
* [Application Performance Team @ T-Systems](https://test-and-integration.t-systems-mms.com/leistungen/application-performance-management.html)
* [Open issues](https://issues.jenkins-ci.org/issues/?jql=project%20%3D%20JENKINS%20AND%20status%20in%20(Open%2C%20%22In%20Progress%22%2C%20Reopened)%20AND%20component%20%3D%20%27performance-signature-dynatrace-plugin%27)
* [Changelog](CHANGELOG.md)
