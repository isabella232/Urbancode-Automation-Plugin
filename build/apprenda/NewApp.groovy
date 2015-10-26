/**
 * 
 */
package com.apprenda.integrations.urbancode

/**
 * @author cdutra
 *
 */
class NewApp {
	// this is required in order to get the right file
	@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
	import groovyx.net.http.RESTClient
	import com.urbancode.air.AirPluginTool
	import static groovyx.net.http.ContentType.*
	final def apTool = new AirPluginTool(this.args[0], this.args[1])
	final def props = apTool.getStepProperties()
	def paths = [Auth:'/authentication/api/v1/sessions/developer', 
		NewVersion:'/developer/api/v1/versions/', 
		NewApp:'/developer/api/v1/apps/'
		GetAliases:'/developer/api/v1/apps/', 
		GetVersions:'/developer/api/v1/versions/', 
		Demote:'/developer/api/v1/versions/']
	
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
	
	
}
