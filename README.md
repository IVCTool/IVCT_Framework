[![gradle-publish](https://github.com/IVCTool/IVCT_Framework/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/IVCTool/IVCT_Framework/actions/workflows/gradle-publish.yml) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/6e7de55a30f049fc917533292a2d35d4)](https://www.codacy.com/gh/IVCTool/IVCT_Framework?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=IVCTool/IVCT_Framework&amp;utm_campaign=Badge_Grade)

# IVCT README

The IVCT_Framework is software to support the **Integration**, **Verification** and **Certification** for HLA Federates. It consists of several core components and services compiled into one tool, called  the **IVCT**. The IVCT tool is a framework to run test cases against systems under test, in order to verify interoperability requirement.

## Installation and running the Software

The simplest way to install and use the software is a docker deployment. Docker is a software deployment framework for containerized components - see [Docker](https://www.docker.com/) for more information. To start the IVCT software you need a so called 'compose' file, and a running docker engine.

    docker-compose up

The compose files are currently maintained in the [IVCT_Operation](https://github.com/IVCTool/IVCT_Compositions) repository. Depending on your requirments, you might need additional components, like your own system under test (SuT) and a HLA-RTI.

The user interface comes in two flavors, a [web based user interface](docs/src/4-5-GUI.adoc), and a [command line interface](docs/src/4-3-commandlinetool.adoc).

## For Developers

The IVCT tool is a open source projects that invites developers to share and contribute software and experience. The IVCT components and concepts are explained in some detail in the [document section](docs/src/Home.adoc). Other ways to contribute is to provide test case implementations for existing or possibly new interoperability requirement.

There is a [tutorial to explain the concepts and the best practices for developing test cases](https://github.com/IVCTool/IVCT_TestSuiteDevelopment).

An important resource for building the IVCT tool is the [HLA Container](https://github.com/hlacontainers) project, donated by [TNO](https://www.tno.nl/en/). This project provides the docker images required for the various RTI connections, which are used by the test case engine.  

## Futher Reading

  - The general concepts of [HLA Certification is explained by the NATO Modelling & Simulation Centre of Excellence](https://www.mscoe.org/nato-hla-certification-home/).
  - [IVCTool Release Notes](docs/src/0-1-ReleaseNotes.adoc)

## LICENCE

Copyright 2019 NATO/OTAN

Licensed under the Apache License, [Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

### List of 3rd party libraries

[List of 3rd party libraries used in the IVCT project](docs/src/7-3rdparty-libraries.adoc)
