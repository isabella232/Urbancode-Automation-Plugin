#!/usr/bin/env groovy

/*
- Licensed Materials - Property of IBM Corp.
- IBM UrbanCode Deploy
- (c) Copyright IBM Corporation 2014. All Rights Reserved.
-
- U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
- GSA ADP Schedule Contract with IBM Corp.
*/

import java.io.FileNotFoundException
import java.io.IOException
import com.urbancode.air.AirPluginTool
import com.urbancode.air.CommandHelper

final def workDir = new File('.').canonicalFile;
final def props = new Properties();
final def inputPropsFile = new File(args[0]);
try {
    inputPropsStream = new FileInputStream(inputPropsFile);
    props.load(inputPropsStream);
}
catch (IOException e) {
    throw new RuntimeException(e);
}

final def api = props['api'];
final def user = props['user'];
final def password = props['password'];
final def selfSigned = props['selfSigned']
final def org = props['org'];
final def space = props['space'];
final def appName = props['appName'];
final def manifest = props['manifest'];
final def domain = props['domain'];
final def subdomain = props['subdomain'];
final def instances = props['instances'];
final def memory = props['memory'];
final def disk = props['disk'];
final def buildpack = props['buildpack'];
final def path = props['path'];
final def stack = props['stack'];
final def timeout = props['timeout'];
final def nostart = props['nostart'];
final def noroute = props['noroute'];
final def nomanifest = props['nomanifest'];
final def nohostname = props['nohostname'];
final def randomroute = props['randomroute'];

def commandHelper = new CommandHelper(workDir);

// Setup path
try {
	def curPath = System.getenv("PATH");
	//println "Current PATH: " + curPath
	def pluginHome = new File(System.getenv("PLUGIN_HOME"))
	println "Setup of path using plugin home: " + pluginHome;
	def binDir = new File(pluginHome, "bin")
	def newPath = curPath+":"+binDir.absolutePath;
	commandHelper.addEnvironmentVariable("PATH", newPath);
	def cfHome = new File(props['PLUGIN_INPUT_PROPS']).parentFile
	println "Setting CF_HOME to: " + cfHome;
	commandHelper.addEnvironmentVariable("CF_HOME", cfHome.toString());
	//commandHelper.printEnvironmentVariables();
} catch(Exception e){
	println "ERROR setting path: ${e.message}"
	System.exit(1)
}

// Set cf api
try {
	def commandArgs = ["cf", "api", api];
    if (selfSigned) {
        commandArgs << "--skip-ssl-validation"
    }
	commandHelper.runCommand("Setting cf target api", commandArgs);
} catch(Exception e){
	println "ERROR setting api: ${e.message}"
	System.exit(1)
}

// Authenticate with user and password
try {
	def commandArgs = ["cf", "auth", user, password];
	commandHelper.runCommand("Authenticating with CloudFoundry", commandArgs);
} catch(Exception e){
	println "ERROR authenticating : ${e.message}"
	System.exit(1)
}

// Set target org
try {
	def commandArgs = ["cf", "target", "-o", org];
	commandHelper.runCommand("Setting CloufFoundry target organization", commandArgs);
} catch(Exception e){
	println "ERROR setting target organization : ${e.message}"
	System.exit(1)
}

// Ensure space exists. create-space does nothing if space
// exists
try {
	def commandArgs = ["cf", "create-space", space];
	commandHelper.runCommand("Creating CloufFoundry space", commandArgs);
} catch(Exception e){
	println "ERROR creating space : ${e.message}"
	System.exit(1)
}

// Set target space
try {
	def commandArgs = ["cf", "target", "-s", space];
	commandHelper.runCommand("Setting CloufFoundry target space", commandArgs);
} catch(Exception e){
	println "ERROR setting target space : ${e.message}"
	System.exit(1)
}

// Push the application
try {
	def commandArgs = ["cf", "push", appName];

	if (buildpack) {
		commandArgs << "-b";
		commandArgs << buildpack;
	}

	if (manifest) {
		commandArgs << "-f";
		commandArgs << manifest;
	}

	if (instances) {
		commandArgs << "-i";
		commandArgs << instances;
	}

	if (memory) {
		commandArgs << "-m";
		commandArgs << memory;
	}
	
	if (disk) {
		commandArgs << "-k";
		commandArgs << disk;
	}

	if (path) {
		commandArgs << "-p" ;
		commandArgs << path;
	}

	if (domain) {
		commandArgs << "-d" ;
		commandArgs << domain;
	}

	if (subdomain) {
		commandArgs << "-n" ;
		commandArgs << subdomain;
	}
	
	if (stack) {
		commandArgs << "-s";
		commandArgs << stack;
	}
	
	if (timeout) {
		commandArgs << "-t";
		commandArgs << timeout;
	}

	if (nostart == "true") {
		commandArgs << "--no-start";
	}

	if (noroute == "true") {
		commandArgs << "--no-route";
	}

	if (nomanifest == "true") {
		commandArgs << "--no-manifest";
	}

	if (nohostname == "true") {
		commandArgs << "--no-hostname";
	}
	
	if (randomroute == "true") {
		commandArgs << "--random-route";
	}

	commandHelper.runCommand("Deploying CloudFoundry application", commandArgs);
} catch(Exception e){
	println "ERROR authenticating : ${e.message}";
	System.exit(1);
}

// Logout
try {
	def commandArgs = ["cf", "logout"];
	commandHelper.runCommand("Logout from CloudFoundry system", commandArgs);
} catch(Exception e){
	println "ERROR logging out : ${e.message}"
	System.exit(1)
}

System.exit(0);
