package main.java
import main.java.urbancode.CommandHelper
import groovy.util.logging.Slf4j

@Slf4j
public class InternalCreateService
{
	final def workDir = new File('.').canonicalFile
	final def props = new Properties()
	final def inputPropsFile = null
	final def api = props['api']
	final def user = props['user']
	final def password = props['password']
	final def selfSigned = props['selfSigned']
	final def org = props['org']
	final def space = props['space']
	final def name = props['name']
	final def service = props['service']
	final def plan = props['plan']
	final def CF_PATH = props['pathToCF']
	def commandHelper = new CommandHelper(workDir);
}

try {
	def service = new InternalCreateService()
	service.inputPropsFile = new File(args[0])
	inputPropsStream = new FileInputStream(inputPropsFile)
	props.load(inputPropsStream)
}
catch (IOException e) {
	log.error("Could not load properties file.", e)
	throw new IOException(e)
}


// Setup path
try {
	def curPath = System.getenv("PATH");
	def pluginHome = new File(System.getenv("PLUGIN_HOME"))
	log.info("Setup of path using plugin home: " + pluginHome)
	def binDir = new File(pluginHome, "bin")
	def newPath = curPath+":"+binDir.absolutePath;
	commandHelper.addEnvironmentVariable("PATH", newPath);
	def cfHome = new File(props['PLUGIN_INPUT_PROPS']).parentFile
	log.info("Setting CF_HOME to: " + cfHome)
	commandHelper.addEnvironmentVariable("CF_HOME", cfHome.toString());
} catch( e){
	log.error("ERROR setting path: ${e.message}",e)
	System.exit(1)
}

// Set cf api
try {
	def commandArgs = [CF_PATH, "api", api];
    if (selfSigned) {
        commandArgs << "--skip-ssl-validation"
    }
	commandHelper.runCommand("Setting cf target api", commandArgs);
} catch( e){
	log.error("ERROR setting api: ${e.message}",e)
	System.exit(1)
}

// Authenticate with user and password
try {
	def commandArgs = [CF_PATH, "auth", user, password];
	commandHelper.runCommand("Authenticating with CloudFoundry", commandArgs);
} catch(e){
	log.error("ERROR authenticating : ${e.message}",e)
	System.exit(1)
}

// Set target org
try {
	def commandArgs = [CF_PATH, "target", "-o", org];
	commandHelper.runCommand("Setting CloufFoundry target organization", commandArgs);
} catch(e){
	log.error("ERROR setting target organization : ${e.message}",e)
	System.exit(1)
}

// Ensure space exists. create-space does nothing if space
// exists
try {
	def commandArgs = [CF_PATH, "create-space", space];
	commandHelper.runCommand("Creating CloufFoundry space", commandArgs);
} catch(e){
	log.error("ERROR creating space : ${e.message}", e)
	System.exit(1)
}

// Set target space
try {
	def commandArgs = [CF_PATH, "target", "-s", space];
	commandHelper.runCommand("Setting CloufFoundry target space", commandArgs);
} catch(e){
	log.error("ERROR setting target space : ${e.message}")
	System.exit(1)
}

// Execute create-service
try {
	def commandArgs = [CF_PATH, "create-service", service, plan, name];
	commandHelper.runCommand("Executing CF create-service", commandArgs);
} catch(e){
	log.error("ERROR executing create-service : ${e.message}")
	System.exit(1);
}

// Logout
try {
	def commandArgs = [CF_PATH, "logout"];
	commandHelper.runCommand("Logout from CloudFoundry system", commandArgs);
} catch(e){
	log.error("ERROR logging out : ${e.message}")
	System.exit(1)
}

System.exit(0);
