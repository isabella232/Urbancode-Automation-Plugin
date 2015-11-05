package com.apprenda.integrations.urbancode.test
import spock.lang.Shared
import spock.lang.Specification
import com.apprenda.integrations.urbancode.*
import com.apprenda.integrations.urbancode.util.*

public class DeployAppTests extends Specification {
	
	 @Shared testProperties = [ApprendaURL:'https://apps.apprenda.heineken',
		ApprendaUser:'fluffy@apprenda.com',
		ApprendaPassword:'password',
		TenantAlias:'warkittens',
		SelfSignedFlag:true,
		AppAlias:'apprendazon',
		ArchiveLocation:'testapps/apprendazon-1.0.zip',
		Stage:'definition']
	 
	 @Shared uc1props
	 @Shared uc2props
	 @Shared uc3props
	 @Shared uc4props
	 @Shared uc5props
	 @Shared uc6props
	 @Shared uc7props
	 @Shared uc8props
	 @Shared uc1vd = ''
	 @Shared uc2vd = ''
	 @Shared uc3vd = ''
	 @Shared uc4vd = ''
	 @Shared uc5vd = ''
	 @Shared uc6vd = ''
	 @Shared uc7vd = ''
	 @Shared uc8vd = ''
	 
	// test version detection
	def setupSpec()
	{
			// loading 8 use cases
			uc1props = testProperties
			uc1props.AppAlias = 'uc1'
			uc2props = testProperties
			uc2props.AppAlias = 'uc2'
			uc3props = testProperties
			uc3props.AppAlias = 'uc3'
			uc4props = testProperties
			uc4props.AppAlias = 'uc4'
			uc5props = testProperties
			uc5props.AppAlias = 'uc5'
			uc6props = testProperties
			uc6props.AppAlias = 'uc6'
			uc7props = testProperties
			uc7props.AppAlias = 'uc7'
			uc8props = testProperties
			uc8props.AppAlias = 'uc8'
			
			def uc1new = ApprendaClient.NewApplication(uc1props, 'v1')
			def uc1patch = ApprendaClient.PatchApplication(uc1props, 'v1')
			// use case 2 - one sandbox
			// ---------------------------------
			def uc2new = ApprendaClient.NewApplication(uc2props, 'v1')
			def uc2patch = ApprendaClient.PatchApplication(uc2props, 'v1')
			def uc2promote = ApprendaClient.Promote(uc2props, 'v1')
			// use case 3 - one published
			// ---------------------------------
			def uc3new = ApprendaClient.NewApplication(uc3props, 'v1')
			def uc3patch = ApprendaClient.PatchApplication(uc3props, 'v1')
			def uc3promote = ApprendaClient.Promote(uc3props, 'v1')
			def uc3promote2 = ApprendaClient.Promote(uc3props, 'v1')
			// use case 4 - one published, one definition
			// ---------------------------------
			def uc4new = ApprendaClient.NewApplication(uc4props, 'v1')
			def uc4patch = ApprendaClient.PatchApplication(uc4props, 'v1')
			def uc4promote = ApprendaClient.Promote(uc4props, 'v1')
			def uc4promote2 = ApprendaClient.Promote(uc4props, 'v1')
			def uc4newversion = ApprendaClient.PostNewVersion(uc4props, 'v2')
			// use case 5 - one published, one sandbox
			// ---------------------------------
			def uc5new = ApprendaClient.NewApplication(uc5props, 'v1')
			def uc5patch = ApprendaClient.PatchApplication(uc5props, 'v1')
			def uc5promote = ApprendaClient.Promote(uc5props, 'v1')
			def uc5promote2 = ApprendaClient.Promote(uc5props, 'v1')
			def uc5newversion = ApprendaClient.PostNewVersion(uc5props, 'v2')
			def uc5promote3 = ApprendaClient.Promote(uc5props, 'v2')
			// use case 6 - one published, two in definition
			// ---------------------------------
			def uc6new = ApprendaClient.NewApplication(uc6props, 'v1')
			def uc6patch = ApprendaClient.PatchApplication(uc6props, 'v1')
			def uc6promote = ApprendaClient.Promote(uc6props, 'v1')
			def uc6promote2 = ApprendaClient.Promote(uc6props, 'v1')
			def uc6newversion = ApprendaClient.PostNewVersion(uc6props, 'v2')
			def uc6newversion2 = ApprendaClient.PostNewVersion(uc6props, 'v3')
			// use case 7 - one published, one sandbox, one definition
			// ---------------------------------
			def uc7new = ApprendaClient.NewApplication(uc7props, 'v1')
			def uc7patch = ApprendaClient.PatchApplication(uc7props, 'v1')
			def uc7promote = ApprendaClient.Promote(uc7props, 'v1')
			def uc7promote2 = ApprendaClient.Promote(uc7props, 'v1')
			def uc7newversion = ApprendaClient.PostNewVersion(uc7props, 'v2')
			def uc7promote3 = ApprendaClient.Promote(uc7props, 'v2')
			def uc7newversion2 = ApprendaClient.PostNewVersion(uc7props, 'v3')
			// use case 8 - one published, two in sandbox
			// --------------------------------
			def uc8new = ApprendaClient.NewApplication(uc8props, 'v1')
			def uc8patch = ApprendaClient.PatchApplication(uc8props, 'v1')
			def uc8promote = ApprendaClient.Promote(uc8props, 'v1')
			def uc8promote2 = ApprendaClient.Promote(uc8props, 'v1')
			def uc8newversion = ApprendaClient.PostNewVersion(uc8props, 'v2')
			def uc8promote3 = ApprendaClient.Promote(uc8props, 'v2')
			def uc8newversion2 = ApprendaClient.PostNewVersion(uc8props, 'v3')
			def uc8promote4 = ApprendaClient.Promote(uc8props, 'v3')
			
			// get data
			def uc1data = ApprendaClient.GetApplicationInfo(uc1props)
			def uc2data = ApprendaClient.GetApplicationInfo(uc2props)
			def uc3data = ApprendaClient.GetApplicationInfo(uc3props)
			def uc4data = ApprendaClient.GetApplicationInfo(uc4props)
			def uc5data = ApprendaClient.GetApplicationInfo(uc5props)
			def uc6data = ApprendaClient.GetApplicationInfo(uc6props)
			def uc7data = ApprendaClient.GetApplicationInfo(uc7props)
			def uc8data = ApprendaClient.GetApplicationInfo(uc8props)
			
			// run version detection
			uc1vd = new DeployApp().detectVersion(uc1data, uc1props)
			uc2vd = new DeployApp().detectVersion(uc2data, uc2props)
			uc3vd = new DeployApp().detectVersion(uc3data, uc3props)
			uc4vd = new DeployApp().detectVersion(uc4data, uc4props)
			uc5vd = new DeployApp().detectVersion(uc5data, uc5props)
			uc6vd = new DeployApp().detectVersion(uc6data, uc6props)
			uc7vd = new DeployApp().detectVersion(uc7data, uc7props)
			uc8vd = new DeployApp().detectVersion(uc8data, uc8props)	
	}
	
	def Test1vDefinition()
	{
		expect:
			uc1vd.newVersionRequired == false
			uc1vd.targetVersion == 'v1'
	}
	
	def Test1vSandbox()
	{
		expect:
			uc2vd.newVersionRequired == false
			uc2vd.targetVersion == 'v1'
	}
	
	def Test1vPublished()
	{
		expect:
			uc3vd.newVersionRequired == true
			uc3vd.targetVersion == 'v2'
	}
	
	def Test2vOnePublishedOneDefinition()
	{
		expect:
			uc4vd.newVersionRequired == false
			uc4vd.targetVersion == 'v2'
	}
	
	def Test2vOnePublishedOneSandbox()
	{
		expect:
			uc5vd.newVersionRequired == false
			uc5vd.targetVersion == 'v2'
	}
	
	def Test3vOnePublished2Definition()
	{
		expect:
			uc6vd.newVersionRequired == false
			uc6vd.targetVersion == 'v3'
	}
	
	def Test3vOneOfEach()
	{
		expect:
			uc7vd.newVersionRequired == false
			uc7vd.targetVersion == 'v3'
	}
	
	def Test3vOnePublished2Sandbox()
	{
		expect:
			uc8vd.newVersionRequired == false
			uc8vd.targetVersion == 'v3'
	}
	
	def cleanupSpec()
	{
		ApprendaClient.DeleteApplication(uc1props)
		ApprendaClient.DeleteApplication(uc2props)
		ApprendaClient.DeleteApplication(uc3props)
		ApprendaClient.DeleteApplication(uc4props)
		ApprendaClient.DeleteApplication(uc5props)
		ApprendaClient.DeleteApplication(uc6props)
		ApprendaClient.DeleteApplication(uc7props)
		ApprendaClient.DeleteApplication(uc8props)
	}
}
