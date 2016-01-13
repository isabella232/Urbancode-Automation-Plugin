package main.java;
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*
import groovy.util.logging.Slf4j

@Slf4j
class ApprendaClient {
	
	static def client = null
	
	private static getInstance(props)
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
		def client = new RESTClient(props.ApprendaURL)
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
		def token = resp.getData().apprendaSessionToken
		client.defaultRequestHeaders.'ApprendaSessionToken' = token
		return client
		}
		catch (e)
		{
			log.error("Caught error during initialization: " + e)
		}
	}
	
	static def GetApplicationInfo(props)
	{
		try
		{
		getInstance(props)
		return client.get(path:'/developer/api/v1/apps/' + props.AppAlias)
		}
		catch (e)
		{
			log.error("Error caught attempting to retrieve application info.", e)
			return null
		}
	}
	
	static def GetVersionInfo(props)
	{
		getInstance(props)
		return client.get(path: '/developer/api/v1/versions/' + props.AppAlias)
	}
	
	static def PostNewVersion(props, targetVersion)
	{
		getInstance(props)
		return client.post(path: Constants.REST_API_PATHS.NewVersion + props.AppAlias, body: [Name: "Version " + targetVersion + " - created by Urbancode", Alias: targetVersion], requestContentType: JSON)
	}
	
	static def PatchApplication(props, version)
	{
		getInstance(props)
		def archive = new File(props.ArchiveName)
		return client.post(path: Constants.REST_API_PATHS.NewVersion + props.AppAlias + "/" + version + "?action=setArchive&stage=" + props.Stage.toLowerCase(), body:archive.bytes, requestContentType:BINARY)
	}
	
	static def Promote(props, version)
	{
		getInstance(props)
		return client.post(path: Constants.REST_API_PATHS.PromoteDemote + props.AppAlias + "/" + version + "?action=promote&stage=" + props.Stage.toLowerCase())
	}
	
	static def Demote(props, version)
	{
		getInstance(props)
		return client.post(path: Constants.REST_API_PATHS.PromoteDemote + props.AppAlias + "/" + version + "?action=demote")
	}
	
	static def NewApplication(props)
	{
		getInstance(props)
		def reqbody = ["Name":props.AppAlias,"Alias":props.AppAlias,"Description":"Created by Apprenda|UrbanCode Deploy"]
		return client.post(path:Constants.REST_API_PATHS.NewApplication, body:reqbody, requestContentType:JSON)
	}
	
	static def DeleteApplication(props)
	{
		getInstance(props)
		def deleteApp = client.delete(path:Constants.REST_API_PATHS.DeleteApplication + "/" + props.AppAlias)
		return deleteApp
	}
	
	static def GetAllAddons(props)
	{
		getInstance(props)
		return client.getAt(path:Constants.REST_API_PATHS.GetAddonInstances)
	}
	
	static def GetAddonInstanceInfo(props)
	{
		getInstance(props)
		return client.getAt(path:Constants.REST_API_PATHS.GetAddonInstances + "/" + props.AddonAlias + "/" + props.AddonInstanceAlias)
	}
	
	static def SetInstanceCount(props, version)
	{
		getInstance(props)
		return client.post(path:Constants.REST_API_PATHS.SetInstanceCount + "/" + props.AppAlias + "/" + version + "/" + props.ComponentAlias + "/scale/" + props.InstanceCount)
	}
	
	static def GetComponentInfo(props, version)
	{
		getInstance(props)
		return client.getAt(path:Constants.REST_API_PATHS.GetComponentInfo + "/" + props.AppAlias + "/" + version)
	}
}