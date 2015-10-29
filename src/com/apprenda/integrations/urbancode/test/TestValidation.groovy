/**
 * 
 */
package com.apprenda.integrations.urbancode.test;
import groovyx.net.http.RESTClient
import com.apprenda.integrations.urbancode.util.Constants;
import static groovyx.net.http.ContentType.*

class TestValidation {
	
	static def client = null
	
	public static getInstance(props)
	{	
		if(client == null){
			client = init(props)
		}
		return client
	}
	
	public TestValidation(props)
	{
		this.client = init(props)
	}
	
	// so we don't have to worry about re-authenticating, we'll do it on initialization of the class
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
		// implement self-healing
		getInstance(props)
		def getApps = client.get(path:'/developer/api/v1/apps/' + props.AppAlias)
		println getApps.getData()
		return getApps.getData()
	}
}
