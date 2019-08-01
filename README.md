[![Build Status](https://travis-ci.org/IVCTool/IVCT_Framework.svg?branch=master)](https://travis-ci.org/IVCTool/IVCT_Framework) Master

[![Build Status](https://travis-ci.org/IVCTool/IVCT_Framework.svg?branch=development)](https://travis-ci.org/IVCTool/IVCT_Framework) Development

# IVCT README

The IVCT_Framework is software to support the **Integration**, **Verification** and **Certification** for HLA Federates. It consists of several core components and services compiled into one tool, called  the **IVCT**. The IVCT tool is a framework to run test cases against systems under test, in order to verify interoperability requirement.

## Installation and running the Software

The simplest way to install and use the software is a docker deployment. Docker is a software deployment framework for containerized components - see https://www.docker.com/ for more information. To start the IVCT software you need a so called 'compose' file, and a running docker engine.

    docker-compose up

The compose files are currently maintained in the [IVCT_Compositions](https://github.com/IVCTool/IVCT_Compositions) repository. Depending on your requirments, you might need additional components, like your own system under test (SuT) and a HLA-RTI.

The user interface comes in two flavors, a [web based user interface](docs/src/Graphical-User-Interface.adoc), and a [command line interface](docs/src/commandlinetool.adoc).

## For Developers

The IVCT tool is a open source projects that invites developers to share and contribute software and experience. The IVCT components and concepts are explained in some detail in the [document section](docs/src/Home.adoc). Other ways to contribute is to provide test case implementations for existing or possibly new interoperability requirement. There is a [tutorial to explain the concepts and the best practices for developing test cases](https://github.com/IVCTool/TS_HelloWorld/wiki) and description of the [test case libraries](docs/src/TcDevelGuide.adoc) to be used.

You can find a list of [test suites](docs/src/testsuite-overview.adoc) which are already available,  currently under development or in planning.

## Futher Reading

The general concepts of [HLA Certification is explained by the NATO Modelling & Simulation Centre of Excellence](https://www.mscoe.org/nato-hla-certification-home/).

The documentation is located inside the Wiki (https://github.com/MSG134/IVCT_Framework/wiki)

## LICENSE

Copyright 2015 NATO MSG-134.
Copyright 2018 NATO MSG-163.

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
