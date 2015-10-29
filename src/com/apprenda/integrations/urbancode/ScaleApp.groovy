/**
 *
 */
package com.apprenda.integrations.urbancode

/**
 * @author cdutra
 *
 */
	// this is required in order to get the right file
import groovyx.net.http.RESTClient
import com.urbancode.air.AirPluginTool
import static groovyx.net.http.ContentType.*
final def apTool = new AirPluginTool(this.args[0], this.args[1])
final def props = apTool.getStepProperties()

def newVersionRequired = false
println "Starting Apprenda Authentication"
def client = new RESTClient(props.ApprendaURL)
// handle authentication
client.handler.failure = client.handler.success
client.ignoreSSLIssues()
def resp = client.post(path:Constants.REST_API_PATHS.Auth, body:[username:props.ApprendaUser, password:props.ApprendaPassword, tenantAlias:props.TenantAlias], requestContentType: JSON)
println "Authentication Response Code: " + resp.status
println "Authentication Response Data: " + resp.getData()
def token = resp.getData().apprendaSessionToken
client.defaultRequestHeaders.'ApprendaSessionToken' = token
println "Authentication Routine Complete"