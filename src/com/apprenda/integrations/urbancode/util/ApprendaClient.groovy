/**
 * Groovy-based Apprenda REST API Wrapper
 * Author: Chris Dutra
 */
package com.apprenda.integrations.urbancode.util;
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
	
	static def GetApplicationInfo(props)
	{
		try
		{
			// implement self-healing
			getInstance(props)
			def getApps = client.get(path:'/developer/api/v1/apps/' + props.AppAlias)
			println getApps.getData()
			return getApps.getData()
		}
		catch(Exception e)
		{
			
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
		def archive = new File(props.ArchiveLocation)
		def patchApp = client.post(path: Constants.REST_API_PATHS.NewVersion + props.AppAlias + "/" + version + "?action=setArchive&stage=" + props.Stage, body:archive.bytes, requestContentType:BINARY)
		println patchApp.status
		println patchApp.getData()
		return patchApp
	}
	
	static def Promote(props, version)
	{
		def promoteVersion = client.post(path: Constants.REST_API_PATHS.PromoteDemote + props.AppAlias + "/" + version + "?action=promote")
		println promoteVersion.status
		println promoteVersion.getData()
		return promoteVersion
	}
	
	static def Demote(props, version)
	{
		def demoteVersion = client.post(path: Constants.REST_API_PATHS.PromoteDemote + props.AppAlias + "/" + version + "?action=demote")
		println demoteVersion.status
		println demoteVersion.getData()
		return demoteVersion
	}

	
	static def NewApplication(props, version)
	{
		def reqbody = ["Name":props.AppAlias,"Alias":props.AppAlias,"Description":"Created by Apprenda|UrbanCode Deploy"]
		def newApplication = client.post(path:Constants.REST_API_PATHS.NewApplication, body:reqbody, requestContentType:JSON)
		println newApplication.status
		println newApplication.getData()
		return newApplication
	}
	
	static def DeleteApplication(props)
	{
		def deleteApp = client.delete(path:Constants.REST_API_PATHS.DeleteApplication + "/" + props.AppAlias)
		return deleteApp
	}
}