package main.java
import groovy.util.logging.Slf4j
import main.java.urbancode.AirPluginTool;
import main.java.urbancode.CommandHelper;
import groovy.io.FileType.*
import groovy.io.FileVisitResult.*
import static groovy.io.FileType.FILES
final def apTool = new AirPluginTool(this.args[0], this.args[1])
final def props = apTool.getStepProperties()

@Slf4j
class InternalBindBluemixComponentToApprendaApp
{	
	def UpdateConfigurationFiles(props)
	{
		def archiveLocation = props.ApprendaArchiveLocation
		def filesToMatch = ['web.xml','app.config', 'web.config', 'Dockerfile']
		new File(archiveLocation).eachFileRecurse(FILES)
		{
			if(filesToMatch.contains(it.name))
			{
				log.info("Found matching: " + it.name)
				CheckAndReplaceTokens(it, props)
			}
		}
		return 0
	}
	
	def CheckAndReplaceTokens(file, props)
	{
		if(file.name == 'web.xml')
		{
			log.info("Updating web.xml")
			UpdateWebXmlFile(file, props)
		}
		if(file.name.contains('.config'))
		{
			log.info("Updating .net config file: " + file.name)
			UpdateDotNetFile(file, props)
		}
		if(file.name == 'Dockerfile')
		{
			log.info("Updating Dockerfile")
			UpdateDockerFile(file, props)
		}
	}
	
	// This method will only update if it finds the token, otherwise it no-ops.
	private def UpdateWebXmlFile(file, props)
	{
		// this is pretty simple. add a context param as a child of web-app
		def parser = new XmlParser()
		parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		def xml = parser.parse(file)
		def webappchildren = xml.children()
		def contextParams = webappchildren.findAll { it.name() == 'context-param' }
		for(contextParam in contextParams)
			{
				log.info(contextParam)
				log.info(contextParam.'param-value')
				log.info(contextParam.'param-value'[0].text())
				if(contextParam.'param-value'[0].text() == '$#UDEPLOY-' + props.alias + '#$')
				{
					log.info("found a match")
					contextParam.'param-value'[0].value = props.url
				}
				else
				{
					log.info("no match")
				}
			}
		// for testing purposes
		def s = new StringWriter()
		def printer = new XmlNodePrinter(new PrintWriter(s))
		printer.preserveWhitespace = true
		printer.print(xml)
		log.info(s)
		def filewriter = new FileWriter(file)
		def w = new XmlNodePrinter(new PrintWriter(filewriter))
		w.preserveWhitespace = true
		w.print(xml)
	}	
	

	private def UpdateDotNetFile(file, props)
	{
		def parser = new XmlParser()
		parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		def xml = parser.parse(file)
		def children = xml.children()
		def appSettings = children.find { it.name() == 'appSettings' }
		def appSettingsChildren = appSettings.children()
		for(child in appSettingsChildren)
		{
			log.info(child)
			log.info(child.@value)
			if(child.@value == '$#UDEPLOY-' + props.alias + '#$')
			{
				log.info("found a match")
				child.@value = props.url
			}
		}
		def s = new StringWriter()
		def printer = new XmlNodePrinter(new PrintWriter(s))
		printer.preserveWhitespace = true
		printer.print(xml)
		log.info(s)
		def filewriter = new FileWriter(file)
		def w = new XmlNodePrinter(new PrintWriter(filewriter))
		w.preserveWhitespace = true
		w.print(xml)
	}
	
	//private def UpdateDockerFile(file, props)
	//{
	//	log.info("Unimplemented method")
	//}
		
	final def workDir = new File('.').canonicalFile;
	def commandHelper = new CommandHelper(workDir);
	// Requirement - we assume that the environment variables are setup by this point.
	def GetBluemixComponentInfo(props)
	{
		try {
			def curPath = System.getenv("PATH");
			def pluginHome = new File(System.getenv("PLUGIN_HOME"))
			log.info("Setup of path using plugin home: " + pluginHome)
			def binDir = new File(pluginHome, "bin")
			def newPath = curPath+":"+binDir.absolutePath;
			commandHelper.addEnvironmentVariable("PATH", newPath)
			def cfHome = new File(props['PLUGIN_INPUT_PROPS']).parentFile
			log.info("Setting CF_HOME to: " + cfHome)
			commandHelper.addEnvironmentVariable("CF_HOME", cfHome.toString());
		} catch(e){
			("ERROR setting path: ${e.message}")
			return null
		}
		try {
			def commandArgs = [props.pathToCF, "api", props.api];
			if (props.selfSigned) {
				commandArgs << "--skip-ssl-validation"
			}
			commandHelper.runCommand("Setting BlueMix target api", commandArgs);
		} catch(e){
			log.info("ERROR setting api: ${e.message}")
			return null
		}
		try{
			// login
			def LoginCommandArgs = [props.pathToCF, "login", "-u", props.user, "-p", props.password, "-o", props.org, "-s", props.space]
			commandHelper.runCommand("Authenticate with BlueMix", LoginCommandArgs)
			// check get app info
			// note, we need to check for the service as well here. 
			def GetAppInfoArgs = ''
			if(props.componentType == 'Application')
			{
				GetAppInfoArgs = [props.pathToCF, "app", props.component]
			}
			else
			{
				GetAppInfoArgs = [props.pathToCF, "service", props.component]
			}
			def appinfo = '' 
			commandHelper.runCommand("Get App Info", GetAppInfoArgs, { p ->
				def reader = new BufferedReader(new InputStreamReader(p.getInputStream()))
				while(true)
				{
					def line = reader.readLine()
					if(line == null) { break}
					appinfo += line
				}})
			// ok cool, it returns a bunch of useful stuff, let's parse it into a groovy dict so we can use it later.
			log.info("appinfo: " + appinfo)
			appinfo = appinfo[(appinfo.indexOf('state:'))..(appinfo.indexOf('last:')-1)]
			// ok let's start processing the output
			def outputList = [:]
			outputList.put('state', (appinfo[6..(appinfo.indexOf('instances:')-1)]).trim())
			outputList.put('urls', (appinfo[(appinfo.indexOf('urls:')+5)..(appinfo.indexOf('last uploaded:')-1)]).trim())
			return outputList		
		}catch(e)
		{
			log.error("Exception found: ", e)
			return null
		}
	}
}

// the goal of this step is to inject connection information into the application about BlueMix components.
try
{
	def internal = new InternalBindBluemixComponentToApprendaApp()
	def applicationInfo = internal.GetBluemixComponentInfo(props)
	if(applicationInfo == null)
	{
		log.info("Error collecting BlueMix information, check the inner stack trace.")
		System.exit 1
	}
	if(applicationInfo.state != 'started')
	{
		log.info("Your BlueMix app is currently not in a running state, check to make sure your app is OK and retry deployment.")
		System.exit 1
	}
	props.put('url', applicationInfo.urls)
	//internal.UpdateConfigurationFiles(props)
	//System.exit(0)
	return
}
catch(FileNotFoundException e)
{
	log.error("Could not load deployment manifest file, check to make sure your build includes this file.", e)
	//System.exit 1
	throw e
}

