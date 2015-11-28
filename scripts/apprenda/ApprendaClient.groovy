package com.apprenda.integrations.urbancode;
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*

class ApprendaClient {
	
	static def client = null
	
	public static getInstance(props)
	{	
		if(client == null){
			client = init(props)
		}
		return client
	}
	
	private static def init(props)
	{
		try
		{
		println props
		def client = new RESTClient(props.ApprendaURL)
		// this suppresses a failure message, we'll handle it separately
		client.handler.failure = client.handler.success
		if (props.SelfSignedFlag)
		{
			println "disabling SSL"
			client.ignoreSSLIssues()
		}
		def resp = client.post(
				path:Constants.REST_API_PATHS.Auth,
				body:[
						username:props.ApprendaUser,
						password:props.ApprendaPassword,
						tenantAlias:props.TenantAlias
					 ],
				requestContentType: JSON)
		println "Authentication Response Code: " + resp.status
		println "Authentication Response Data: " + resp.getData()
		def token = resp.getData().apprendaSessionToken
		client.defaultRequestHeaders.'ApprendaSessionToken' = token
		println "Authentication Routine Complete"
		return client
		}
		catch (Exception e)
		{
			println "Caught error during initialization: " + e
		}
	}
	
	static def GetApplicationInfo(props)
	{
		try
		{
			// implement self-healing
			getInstance(props)
			def getApps = client.get(path:'/developer/api/v1/apps/' + props.AppAlias)
			println getApps.status
			println getApps.getData()
			return getApps.getData()
		}
		catch(Exception e)
		{
			println "Error trying to retrieve applicaiton info." + e
			return null
		}
	}
	
	static def GetVersionInfo(props)
	{
		getInstance(props)
		def getVersions = client.get(path: '/developer/api/v1/versions/' + props.AppAlias)
		//println "getVersions Response Code: " + getVersions.status
		//println "getVersions Response Data: " + getVersions.getData()
		return getVersions.getData()
	}
	
	static def PostNewVersion(props, targetVersion)
	{
		getInstance(props)
		def newVersion = client.post(path: Constants.REST_API_PATHS.NewVersion + props.AppAlias, body: [Name: "Version " + targetVersion + " - created by Urbancode", Alias: targetVersion], requestContentType: JSON)
		println "DEBUG: newVersion status code: " + newVersion.status
		println "DEBUG: newVersion response data: " + newVersion.getData()
		return newVersion
	}
	
	static def PatchApplication(props, version)
	{
		getInstance(props)
		def archive = new File(props.ArchiveLocation)
		def patchApp = client.post(path: Constants.REST_API_PATHS.NewVersion + props.AppAlias + "/" + version + "?action=setArchive&stage=" + props.Stage, body:archive.bytes, requestContentType:BINARY)
		println patchApp.status
		println patchApp.getData()
		return patchApp
	}
	
	static def Promote(props, version)
	{
		getInstance(props)
		def promoteVersion = client.post(path: Constants.REST_API_PATHS.PromoteDemote + props.AppAlias + "/" + version + "?action=promote")
		println promoteVersion.status
		println promoteVersion.getData()
		return promoteVersion
	}
	
	static def Demote(props, version)
	{
		getInstance(props)
		def demoteVersion = client.post(path: Constants.REST_API_PATHS.PromoteDemote + props.AppAlias + "/" + version + "?action=demote")
		println demoteVersion.status
		println demoteVersion.getData()
		return demoteVersion
	}

	static def NewApplication(props)
	{
		return NewApplication(props, 'v1')
	}
	
	static def NewApplication(props, version)
	{
		getInstance(props)
		def reqbody = ["Name":props.AppAlias,"Alias":props.AppAlias,"Description":"Created by Apprenda|UrbanCode Deploy"]
		def newApplication = client.post(path:Constants.REST_API_PATHS.NewApplication, body:reqbody, requestContentType:JSON)
		println newApplication.status
		println newApplication.getData()
		return newApplication
	}
	
	static def DeleteApplication(props)
	{
		getInstance(props)
		def deleteApp = client.delete(path:Constants.REST_API_PATHS.DeleteApplication + "/" + props.AppAlias)
		return deleteApp
	}
	
	static def GetAddonInstanceInfo(props)
	{
		getInstance(props)
		def getAddonInstance = client.getAt(path:Constants.REST_API_PATHS.GetAddonInstances + "/" + props.AddonAlias + "/" + props.AddonInstanceAlias)
		return getAddonInstance
	}
	
	// for this, we're expecting a 204 back.
	static def SetInstanceCount(props, version)
	{
		getInstance(props)
		def bodypath = Constants.REST_API_PATHS.SetInstanceCount + "/" + props.AppAlias + "/" + version + "/" + props.ComponentAlias + "/scale/" + props.InstanceCount
		println bodypath
		def setInstanceCount = client.post(path:bodypath)
		return setInstanceCount
	}
	
	static def GetComponentInfo(props, version)
	{
		getInstance(props)
		def bodypath = Constants.REST_API_PATHS.GetComponentInfo + "/" + props.AppAlias + "/" + version
		println bodypath
		def getInfo = client.getAt(path:bodypath)
		println getInfo
		return getInfo
	}
}