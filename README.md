# Performance Signature with Dynatrace (AppMon) for Jenkins
[![Build Status](https://ci.jenkins.io/job/Plugins/job/performance-signature-dynatrace-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/performance-signature-dynatrace-plugin/job/master/)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/performance-signature-dynatracesaas.svg)](https://plugins.jenkins.io/performance-signature-dynatracesaas/)
[![Changelog](https://img.shields.io/github/v/tag/jenkinsci/performance-signature-dynatrace-plugin?label=changelog)](https://github.com/jenkinsci/performance-signature-dynatrace-plugin/blob/master/CHANGELOG.md)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/performance-signature-ui?color=blue)](https://plugins.jenkins.io/performance-signature-dynatrace/)

Current software products are created with the help of flexible and agile systems in a continuous integration (CI) environment. Such environments include a
CI-Server like Jenkins. The Performance Signature collects performance values during a build and evaluates and compares the result with previous builds and
non-functional requirements. Several software tests can be run and evaluated automatically, so that the most important key performance indicators (KPI) can be
summarized and be available for all project participants very quickly.

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
  * [Dynatrace SaaS/Managed Documentation](#dynatrace-saasmanaged-documentation)
  * [Links](#links)

<!-- tocstop -->

## Introduction

The Performance Signature evaluates aggregated load and performance data from Dynatrace after each build.
We are currently supporting three data sources:
* Dynatrace Application Monitoring (AppMon)
* Dynatrace SaaS/Manged
* Remote Jenkins Jobs with enabled Performance Signature

Each data source has it's own Jenkins plugin, whereof each one depends on the Performance Signature UI Plugin.
Find below the documentation of each component.

* **[Performance Signature: Dynatrace SaaS/Managed](dynatrace/README.md)**
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

Contact the Performance Experts at T-Systems: `performance@t-systems-mms.com` for professional support or consultancy. T-Systems offers support packages starting at 500 € p.a.

## Additional Resources

### Dynatrace SaaS/Managed Documentation

- https://www.dynatrace.com/support/help/

### Links

* [Dynatrace Plugin Page](https://community.dynatrace.com/community/display/DL/Performance+Signature+Plugin)
* [Application Performance Team @ T-Systems](https://test-and-integration.t-systems-mms.com/leistungen/application-performance-management.html)
* [Open issues](https://issues.jenkins-ci.org/issues/?jql=project%20%3D%20JENKINS%20AND%20status%20in%20(Open%2C%20%22In%20Progress%22%2C%20Reopened)%20AND%20component%20%3D%20%27performance-signature-dynatrace-plugin%27)
* [Changelog](CHANGELOG.md)
