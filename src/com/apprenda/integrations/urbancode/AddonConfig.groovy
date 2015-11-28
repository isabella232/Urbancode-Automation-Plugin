package com.apprenda.integrations.urbancode
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
def resp = client.post(path:paths.Auth, body:[username:props.ApprendaUser, password:props.ApprendaPassword, tenantAlias:props.TenantAlias], requestContentType: JSON)
println "Authentication Response Code: " + resp.status
println "Authentication Response Data: " + resp.getData()
def token = resp.getData().apprendaSessionToken
client.defaultRequestHeaders.'ApprendaSessionToken' = token
println "Authentication Routine Complete"

// so, for the addon configuration, we'll grab the addon info to make sure the addon exists. 

// then we'll determine if:
// 	- did we specify an instance to use? 
//		- if so, specify if the instance exists. if yes, proceed to the XML configuration piece
//		- if not, check the rules. if the "create new instance if it doesn't exist" checkbox, deploy a new instance and move on.
//			-- otherwise, fail.

def getAddons = client.get(path:paths.GetAddon + props.addonAlias)
println getAddons.status
println getAddons.getData()

def getAddonInstance = client.get(path:paths.GetAddonInstance + props.addonAlias + "/" + props.addonInstanceAlias)
	println getAddonInstance.status
	println getAddonsInstance.getData()