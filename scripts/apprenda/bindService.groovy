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
final def selfSigned = props['selfSigned'];
final def org = props['org'];
final def space = props['space'];
final def name = props['name'];
final def service = props['service'];
final def application = props['application'];

def commandHelper = new CommandHelper(workDir);

// Setup path
try {
	def curPath = System.getenv("PATH");
	def pluginHome = new File(System.getenv("PLUGIN_HOME"))
	println "Setup of path using plugin home: " + pluginHome;
	def binDir = new File(pluginHome, "bin")
	def newPath = curPath+":"+binDir.absolutePath;
	commandHelper.addEnvironmentVariable("PATH", newPath);
	def cfHome = new File(props['PLUGIN_INPUT_PROPS']).parentFile
	println "Setting CF_HOME to: " + cfHome;
	commandHelper.addEnvironmentVariable("CF_HOME", cfHome.toString());
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

// Execute bind-service
try {
	def commandArgs = ["cf", "bind-service", application, service];
	commandHelper.runCommand("Executing CF bind-service", commandArgs);
} catch(Exception e){
	println "ERROR executing bind-service : ${e.message}";
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
