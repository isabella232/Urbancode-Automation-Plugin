# Urbancode Deploy Integration

## Overview

This project provides an integration for IBM Urbancode Deploy and Apprenda, enabling developers to be able to push their applications to Apprenda using the release manager capabilities that UrbanCode Deploy provides. This integration allows for uDeploy customers to target their Apprenda environments as resources, and the enclosed automation plugin provides the steps necessary to interact with the Apprenda environment.

In addition, the automation plugin provides the ability for Apprenda applications to consume components and services from IBM Bluemix, enabling hybrid applications to run on both platform as as service (PaaS) environments.

This has been tested to work on the Apprenda platform versions 6.5.1/2/3, and works on Urbancode 6.1.x and up, inlcuding 6.2.2. 

## License

Copyright (c) 2016 Apprenda Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.


## Building from source

mvn clean install (from the root working directory)

## Running Tests

mvn clean site test (from the root working directory)

The "site" goal will generate the assets needed to generate a full HTML output of the surefile tests. 

## Documentation

Please refer to the docs folder for a complete guide on how to install, configure, and manage the integration.
