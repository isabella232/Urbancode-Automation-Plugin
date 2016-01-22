package main.java
import main.java.urbancode.AirPluginTool
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseException;

import static groovyx.net.http.ContentType.*

@Slf4j
class InternalScaleApp
{
	def PerformScaleAction(props, version)
	{
		try
		{
			
			def scaleResp = ApprendaClient.SetInstanceCount(props, version)
			// make sure it comes back 204, else throw error
			if(scaleResp.status != 204)
			{
				throw new HttpResponseException("Scaling failed. Contact your platform operator for assistance.")
			}
		}catch(e)
		{
			log.error("Issue encountered during scaling action", e)
			throw e
		}

	}
	
	static def logError(exception)
	{
		log.error("Could not scale component for application, please refer to the error messages and exceptions for more information.", exception)
	}
	
	static def logInfo(data)
	{
		log.info(data.toString())
	}
}

final def apTool = new AirPluginTool(this.args[0], this.args[1])
final def props = apTool.getStepProperties()
try
{
	def getApps = ApprendaClient.GetApplicationInfo(props)
	InternalScaleApp.logInfo(getApps.getData())
	def version = getApps.getData().currentVersion.alias
	if(getApps.getData().currentVersion.stage != 'Published')
	{
		InternalScaleApp.logInfo("This application is not running in published, and scaling for non-published apps is currently not supported.")
		return
	}
	def components = ApprendaClient.GetComponentInfo(props, version)
	InternalScaleApp.logInfo(components.getData())
	InternalScaleApp.logInfo("Starting scale for component: " + props.ComponentAlias)
	def output = new InternalScaleApp().PerformScaleAction(props, version)
}
catch (e)
{
	InternalScaleApp.logError(e)
	throw e
}