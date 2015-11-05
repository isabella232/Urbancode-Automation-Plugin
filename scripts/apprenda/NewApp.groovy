package com.apprenda.integrations.urbancode
import groovyx.net.http.RESTClient
import com.urbancode.air.AirPluginTool
import static groovyx.net.http.ContentType.*
import com.apprenda.integrations.urbancode.util.*
public class NewApp
{
	public static void main(String args)
	{
		try
		{
			final def apTool = new AirPluginTool(this.args[0], this.args[1])
			final def props = apTool.getStepProperties()
			def newApplication = ApprendaClient.NewApplication(props)
		}
		catch(Exception e)
		{
			println "Error creating new application in Apprenda"
			println  e.message
			e.printStackTrace()
			System.exit 1
		}
		System.exit 0
	}
}
