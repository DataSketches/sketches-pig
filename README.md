<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->

[![Build Status](https://travis-ci.org/apache/incubator-datasketches-pig.svg?branch=master)](https://travis-ci.org/apache/incubator-datasketches-pig)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.datasketches/datasketches-pig/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.apache.datasketches/datasketches-pig)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/apache/incubator-datasketches-pig.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/apache/incubator-datasketches-pig/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/apache/incubator-datasketches-pig.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/apache/incubator-datasketches-pig/alerts/)
[![Coverage Status](https://coveralls.io/repos/github/apache/incubator-datasketches-pig/badge.svg?branch=master&service=github)](https://coveralls.io/github/apache/incubator-datasketches-pig?branch=master)

=================

# DataSketches UDF/UDAF Adaptors for Apache Pig 
Please refer to our [website](https://datasketches.apache.org) for more comprehensive information about the various sketching algorithms and how to use them.

## Documentation

### [DataSketches Library Website](https://datasketches.apache.org/)

### [Java Core Overview](https://datasketches.apache.org/docs/TheChallenge.html)

### [Java Core Javadocs](https://datasketches.apache.org/api/core/snapshot/apidocs/index.html)

### Hadoop Pig UDFs 
See relevant sections under the different sketch types in Java Core Overview


## Downloading Latest Release
__NOTE:__ This component accesses resource files for testing. As a result, the directory elements of the full absolute path of the target installation directory 
    must qualify as Java identifiers. In other words, the directory elements must not have any space characters (or non-Java identifier characters) in any of the path elements.
    
This is required by the Oracle Java Specification in order to ensure location-independent 
    access to resources: [See Oracle Location-Independent Access to Resources](https://docs.oracle.com/javase/8/docs/technotes/guides/lang/resources.html)

### [Zip File from www.apache.org/dist](http://www.apache.org/dist/incubator/datasketches/pig/)

### [Jar Files from Maven Central](https://repository.apache.org/content/repositories/releases/org/apache/datasketches/datasketches-pig/)

### [GitHub](https://github.com/apache/incubator-datasketches-pig/releases)

## Build Instructions

### JDK8 is required to compile
This DataSketches component is pure Java and you must compile using JDK 8.

### Recommended Build Tool
This DataSketches component is structured as a Maven project and Maven is the recommended Build Tool.

There are two types of tests: normal unit tests and tests run by the strict profile.  

To run normal unit tests:

    $ mvn clean test

To run the strict profile tests:

    $ mvn clean test -P strict

To install jars built from the downloaded source:

    $ mvn clean install -DskipTests=true

This will create the following jars:

* datasketches-pig-X.Y.Z-incubating.jar The compiled main class files.
* datasketches-pig-X.Y.Z-incubating-tests.jar The compiled test class files.
* datasketches-pig-X.Y.Z-incubating-sources.jar The main source files.
* datasketches-pig-X.Y.Z-incubating-test-sources.jar The test source files
* datasketches-pig-X.Y.Z-incubating-javadoc.jar  The compressed Javadocs.

### Dependencies

#### Run-time
This has the following top-level dependencies:

* org.apache.datasketches : datasketches-java
* org.apache.pig : pig
* org.apache.hadoop : hadoop-common
* org.apache.commons : commons-math3

#### Testing
See the pom.xml file for test dependencies.

## How to Contact Us
* We have two ASF [the-ASF.slack.com](http://the-ASF.slack.com) slack channels:
    * datasketches -- general user questions
    * datasketches-dev -- similar to our Apache [Developers Mail list](https://lists.apache.org/list.html?dev@datasketches.apache.org), except more interactive, but not as easily searchable.

* For bugs and performance issues: [Issues for datasketches-pig](https://github.com/apache/incubator-datasketches-pig/issues) 

* For general questions about using the library please subscribe: [Users Mail List](https://lists.apache.org/list.html?users@datasketches.apache.org)

* If you are interested in contributing please subscribe: [Developers Mail list](https://lists.apache.org/list.html?dev@datasketches.apache.org)
