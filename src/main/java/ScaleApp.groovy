package main.java
import groovyx.net.http.RESTClient
import main.java.urbancode.AirPluginTool;

import static groovyx.net.http.ContentType.*

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
				throw new Exception("Scaling failed. Contact your platform operator for assistance.")
			}
		}catch(Exception e)
		{
			throw new Exception("Exception occurred during scaling", e)
		}

	}
}

final def apTool = new AirPluginTool(this.args[0], this.args[1])
final def props = apTool.getStepProperties()
try
{
	def getApps = ApprendaClient.GetApplicationInfo(props)
	println getApps
	def version = getApps.currentVersion.alias
	if(getApps.currentVersion.stage != 'Published')
	{
		println "This application is not running in published, and scaling for non-published apps is currently not supported."
		System.exit(1)
	}
	def components = ApprendaClient.GetComponentInfo(props, version)
	println components.getData()
	println "Starting scale for component: " + props.ComponentAlias
	def output = InternalScaleApp().PerformScaleAction(props, version)
	println output
}
catch (Exception e)
{
	println e
	println "Could not scale component for application, please refer to the error messages and exceptions for more information."
	System.exit(1)	
}