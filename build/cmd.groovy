package com.apprenda.integrations.urbancode

// this is required in order to get the right file
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
import groovyx.net.http.RESTClient
import com.urbancode.air.AirPluginTool
import static groovyx.net.http.ContentType.*
final def apTool = new AirPluginTool(this.args[0], this.args[1])
final def props = apTool.getStepProperties()
def paths = [Auth:'/authentication/api/v1/sessions/developer', NewVersion:'/developer/api/v1/versions/', GetAliases:'/developer/api/v1/apps/', GetVersions:'/developer/api/v1/versions/', Demote:'/developer/api/v1/versions/']
try {
    def newVersionRequired = false
	println "Starting Apprenda Authentication"
	def client = new RESTClient(props.ApprendaURL)
    // handle authentication
	client.handler.failure = client.handler.success
    client.ignoreSSLIssues()
	def resp = client.post(path:paths.Auth, body:[username:props.ApprendaUser, password:props.ApprendaPassword, tenantAlias:props.TenantAlias], requestContentType: JSON)
    println "Authentication Response Code: " + resp.status
	println "Authentication Response Data: " + resp.getData()
	def token = resp.getData().apprendaSessionToken
    client.defaultRequestHeaders.'ApprendaSessionToken' = token
	println "Authentication Routine Complete"
	
    println "Starting GET application info and current version"
	def getApps = client.get(path:'/developer/api/v1/apps/' + props.AppAlias)
	println "GetApps Response Code: " + getApps.status
	println "GetApps Response Data: " + getApps.getData()
    def currentVersion = getApps.getData().currentVersion
	println "Begin smart version detection"
	def targetVersion = currentVersion.alias
	def newVerStage = currentVersion.stage
	// so if we aren't just a v1 in definition / sandbox, we have some work to do.
	if(currentVersion.stage == 'Published')
    {
        def oldVerNo = currentVersion.alias.substring(1).toInteger()
        def newVerNo = oldVerNo
        println "DEBUG: oldVerNo: " + oldVerNo + " newVerNo: " + newVerNo
		def getVersions = client.get(path: '/developer/api/v1/versions/' + props.AppAlias)
		println "getVersions Response Code: " + getVersions.status
		println "getVersions Response Data: " + getVersions.getData()
        def versions = getVersions.getData()
        versions.each { version ->
            def verNo = currentVersion.alias.substring(1).toInteger()
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
    }
    println "DEBUG: targetVersion: " + targetVersion
    println "DEBUG: newVersionRequired: " + newVersionRequired
	// ==================
	// Pre-Requisites
	// ==================
	// create new version if needed
    if(newVersionRequired) {
        def newVersion = client.post(path: paths.NewVersion + props.AppAlias, body: [Name: "Version " + targetVersion + " - created by Urbancode", Alias: targetVersion], requestContentType: JSON)
        println "DEBUG: newVersion status code: " + newVersion.status
        println "DEBUG: newVersion response data: " + newVersion.getData()
    }
	else
	if(currentVersion.stage == 'Sandbox' || newVerStage == 'Sandbox')
	{
		println "Target version is in Sandbox stage. Demoting to defintion to begin patching..."
		def demoteVersion = client.post(path: paths.Demote + props.AppAlias + "/" + targetVersion + "?action=demote")
		println demoteVersion.status
		println demoteVersion.getData()
	}
    // patching the application version
    def archive = new File(props.ArchiveLocation)
    def patchApp = client.post(path: paths.NewVersion + props.AppAlias + "/" + targetVersion + "?action=setArchive&stage=" + props.Stage, body:archive.bytes, requestContentType:BINARY)
    println patchApp.status
    println patchApp.getData()
}
catch (Exception e) {
    println "Error during deployment to Apprenda"
    println  e.message
    e.printStackTrace()
    System.exit 1
}
System.exit 0
     
