package com.apprenda.integrations.urbancode
import jdk.internal.dynalink.beans.CallerSensitiveDetector.DetectionStrategy

import com.urbancode.air.AirPluginTool

public class InternalDeployApp {
	
	def detectVersion(versionInfo, props)
	{
		// use case 4: 
		// [alias:uc4, currentVersion:[alias:v1, stage:Published]]
		// [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc4', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition']
		// [[alias:v1, stage:Published], [alias:v2, stage:Definition]]
		def newVersionRequired = false
		def targetVersion = versionInfo.currentVersion.alias
		def newVerStage = versionInfo.currentVersion.stage
		// case: current version is either in definition or sandbox (guarantees we are working with v1)
		if(versionInfo.currentVersion.stage == 'Definition' || versionInfo.currentVersion.stage == 'Sandbox')
		{
			return [newVersionRequired:false, targetVersion:'v1', newVerStage:versionInfo.currentVersion.stage]
		}
		// case: the current version is published.
		else if(versionInfo.currentVersion.stage == 'Published')
		{
			def oldVerNo = versionInfo.currentVersion.alias.substring(1).toInteger()
			def newVerNo = oldVerNo
			println "DEBUG: oldVerNo: " + oldVerNo + " newVerNo: " + newVerNo
			
			def versions = ApprendaClient.GetVersionInfo(props)
			println versions
			versions.each { version ->
				def verNo = version.alias.substring(1).toInteger()
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
		def versionOutput = internal.detectVersion(getApps, props)
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