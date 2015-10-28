/**
 * 
 */
package com.apprenda.integrations.urbancode.test;

import groovyx.net.http.RESTClient

/**
 * @author cdutra
 *
 */
public class TestValidation {
	
	static def client = null
	
	public TestValidation(props)
	{
		this.client = init(props)
	}
	
	// so we don't have to worry about reauthenticating, we'll do it on intialization of the class
	private def init(props)
	{
		def client = new RESTClient(props.ApprendaURL)
		// this suppresses a failure message, we'll handle it separately
		client.handler.failure = client.handler.success
		if (props.SelfSignedFlag)
		{
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
	
	def GetApplications()
	{
		
	}
	
	def GetApplicationVersions()
	{
		
	}
}
