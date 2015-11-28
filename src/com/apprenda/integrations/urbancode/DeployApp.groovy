package com.apprenda.integrations.urbancode
import jdk.internal.dynalink.beans.CallerSensitiveDetector.DetectionStrategy

import com.urbancode.air.AirPluginTool


public class InternalDeployApp {
	def detectVersion(versionInfo, props)
	{
		def newVersionRequired = false
		def targetVersion = versionInfo.currentVersion.alias
		def newVerStage = versionInfo.currentVersion.stage
		if(versionInfo.currentVersion.stage == 'Definition' || versionInfo.currentVersion.stage == 'Sandbox')
		{
			return [newVersionRequired:false, targetVersion:'v1', newVerStage:versionInfo.currentVersion.stage]
		}
		else if(versionInfo.currentVersion.stage == 'Published')
		{
			def oldVerNo = versionInfo.currentVersion.alias.substring(1).toInteger()
			def newVerNo = oldVerNo
			println "DEBUG: oldVerNo: " + oldVerNo + " newVerNo: " + newVerNo
			
			// check to make sure the app exists. if not, create it.
			def appInfo = ApprendaClient.GetApplicationInfo(props)			
			def versions = ApprendaClient.GetVersionInfo(props)
			println versions
			versions.each { version ->
				def verNo = version.alias.substring(1).toInteger()
				if (newVerNo < verNo) {
					newVerNo = verNo
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
		else
		{
			throw new Exception("We received data we didn't expect.")
		}
	}
}

	final def apTool = new AirPluginTool(this.args[0], this.args[1])
	props = apTool.getStepProperties()
	try {
		internal = new InternalDeployApp()
		def getApps = ApprendaClient.GetApplicationInfo(props)
		
		// inject here that if we don't have a new application, we need to create it.
		if(getApps == null)
		{
			def newApplication = ApprendaClient.NewApplication(props)
			// should come back now as v1 and definition
			getApps = ApprendaClient.GetApplicationInfo(props)
		}
		def versionOutput = internal.detectVersion(getApps, props)
		if(versionOutput.newVersionRequired) 
		{
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