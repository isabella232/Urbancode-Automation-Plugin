package main.java
import main.java.urbancode.AirPluginTool
import groovy.util.logging.Slf4j

@Slf4j
public class InternalDeployApp {
	def detectVersion(versionInfo, props)
	{
		log.info("Starting version detection.")
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
			def versions = ApprendaClient.GetVersionInfo(props)
			versions.getData().each { version ->
				def verNo = version.alias.substring(1).toInteger()
				if (newVerNo < verNo) {
					newVerNo = verNo
					newVerStage = version.stage
					if(newVerStage != 'Published')
					{
						newVersionRequired = false
					}
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
			log.error("We received data we didn't expect. Exiting...")
			return null
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
			ApprendaClient.NewApplication(props)
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
			ApprendaClient.Demote(props, versionOutput.targetVersion)	
		}
		ApprendaClient.PatchApplication(props, versionOutput.targetVersion)
		if(props.Stage == 'Sandbox' || props.Stage == 'Published')
		{
			ApprendaClient.Promote(props, versionOutput.targetVersion)
		}
	}
	catch (e) {
		log.error(e)
		System.exit 1
	}
	System.exit 0