package com.apprenda.integrations.urbancode
import groovyx.net.http.RESTClient
import com.apprenda.integrations.urbancode.util.ApprendaClient
import com.apprenda.integrations.urbancode.util.Constants
import com.urbancode.air.AirPluginTool
import static groovyx.net.http.ContentType.*

public class DeployApp {
	private def detectVersion(versionInfo)
	{
		println "Begin smart version detection"
		def newVersionRequired = false
		def targetVersion = versionInfo.currentVersion.alias
		def newVerStage = versionInfo.currentVersion.stage
		// so if we aren't just a v1 in definition / sandbox, we have some work to do.
		if(versionInfo.currentVersion.stage == 'Published')
		{
			def oldVerNo = versionInfo.currentVersion.alias.substring(1).toInteger()
			def newVerNo = oldVerNo
			println "DEBUG: oldVerNo: " + oldVerNo + " newVerNo: " + newVerNo
			
			def versions = ApprendaClient.GetVersionInfo(props)
			versions.each { version ->
				def verNo = versioninfo.currentVersion.alias.substring(1).toInteger()
				if (newVerNo < verNo) {
					newVerNo = verNo
					// get stage. if we're in sandbox, we have to demote
					newVerStage = version.stage
					if(newVerStage != 'Published')
					{
						newVersionRequired = false
					}
					println "DEBUG: After iteration:  newVerNo: " + newVerNo + " newVerStage: " + newVerStage
					println "DEBUG: After iteration:  newVersionRequired: " + newVersionRequired
				}
			}
			if(newVerNo == oldVerNo && (newVerStage == 'Published'))
			{
				newVersionRequired = true
				newVerNo++
			}
			targetVersion = "v" + newVerNo.toString()
			return [newVersionRequired:newVersionRequired, targetVersion:targetVersion, newVerStage:newVerStage]
		}
	}
	
	public static void main(String args)
	{
		final def apTool = new AirPluginTool(this.args[0], this.args[1])
		final def props = apTool.getStepProperties()
		try {
			def getApps = ApprendaClient.getApplicationInfo(props)
			def versionOutput = detectVersion(getApps)
			if(versionOutput.newVersionRequired) {
				ApprendaClient.PostNewVersion(props, versionOutput.targetVersion)
			}
			else
			if(getApps.currentVersion.stage == 'Sandbox' || versionOutput.newVerStage == 'Sandbox')
			{
				println "Target version is in Sandbox stage. Demoting to defintion to begin patching..."
				ApprendaClient.Demote(props, versionOutput.targetVersion)	
			}
			ApprendaClient.PatchApplication(props, versionOutput.targetVersion)
		}
		catch (Exception e) {
			println "Error during deployment to Apprenda"
			println  e.message
			e.printStackTrace()
			System.exit 1
		}
		System.exit 0
	}
}