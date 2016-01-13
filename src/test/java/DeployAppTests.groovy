package test.java
import spock.lang.Shared
import spock.lang.Specification
import main.java.*
import groovy.util.logging.Slf4j

@Slf4j
public class DeployAppTests extends Specification {
	 
	@Shared uc1props = [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc1', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition']
	@Shared uc2props = [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc2', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition']
	@Shared uc3props = [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc3', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition']
	@Shared uc4props = [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc4', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition']
	@Shared uc5props = [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc5', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition']
	@Shared uc6props = [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc6', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition']
	@Shared uc7props = [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc7', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition']
	@Shared uc8props = [ApprendaURL:'https://apps.apprenda.heineken', ApprendaUser:'fluffy@apprenda.com', ApprendaPassword:'password', TenantAlias:'warkittens', SelfSignedFlag:true, AppAlias:'uc8', ArchiveLocation:'testapps/apprendazon-1.0.zip', Stage:'definition'] 
	
	def Test1vDefinition()
	{
		when:
			ApprendaClient.NewApplication(uc1props, 'v1')
			ApprendaClient.PatchApplication(uc1props, 'v1')
			def uc1data = ApprendaClient.GetApplicationInfo(uc1props)
			def uc1vd = new InternalDeployApp().detectVersion(uc1data, uc1props)
		then:
			uc1vd.newVersionRequired == false
			uc1vd.targetVersion == 'v1'
			notThrown Exception
	}
	
	def Test1vSandbox()
	{
		when:
			ApprendaClient.NewApplication(uc2props, 'v1')
			ApprendaClient.PatchApplication(uc2props, 'v1')
			ApprendaClient.Promote(uc2props, 'v1')
			def uc2data = ApprendaClient.GetApplicationInfo(uc2props)
			def uc2vd = new InternalDeployApp().detectVersion(uc2data, uc2props)
		then:
			uc2vd.newVersionRequired == false
			uc2vd.targetVersion == 'v1'
			notThrown Exception
	}
	
	def Test1vPublished()
	{
		when:
			ApprendaClient.NewApplication(uc3props, 'v1')
			ApprendaClient.PatchApplication(uc3props, 'v1')
			ApprendaClient.Promote(uc3props, 'v1')
			ApprendaClient.Promote(uc3props, 'v1')
			def uc3data = ApprendaClient.GetApplicationInfo(uc3props)
			def uc3vd = new InternalDeployApp().detectVersion(uc3data, uc3props)
		then:
			uc3vd.newVersionRequired == true
			uc3vd.targetVersion == 'v2'
			notThrown Exception
	}
	
	def Test2vOnePublishedOneDefinition()
	{
		when:
			ApprendaClient.NewApplication(uc4props, 'v1')
			ApprendaClient.PatchApplication(uc4props, 'v1')
			ApprendaClient.Promote(uc4props, 'v1')
			ApprendaClient.Promote(uc4props, 'v1')
			ApprendaClient.PostNewVersion(uc4props, 'v2')
			ApprendaClient.PatchApplication(uc4props, 'v2')
			def uc4data = ApprendaClient.GetApplicationInfo(uc4props)
			def uc4vd = new InternalDeployApp().detectVersion(uc4data, uc4props)
		then:
			uc4vd.newVersionRequired == false
			uc4vd.targetVersion == 'v2'
			notThrown Exception
	}
	
	def Test2vOnePublishedOneSandbox()
	{
		when:
			ApprendaClient.NewApplication(uc5props, 'v1')
			ApprendaClient.PatchApplication(uc5props, 'v1')
			ApprendaClient.Promote(uc5props, 'v1')
			ApprendaClient.Promote(uc5props, 'v1')
			ApprendaClient.PostNewVersion(uc5props, 'v2')
			ApprendaClient.PatchApplication(uc5props, 'v2')
			ApprendaClient.Promote(uc5props, 'v2')
			def uc5data = ApprendaClient.GetApplicationInfo(uc5props)
			def uc5vd = new InternalDeployApp().detectVersion(uc5data, uc5props)
		then:
			uc5vd.newVersionRequired == false
			uc5vd.targetVersion == 'v2'
			notThrown Exception
	}
	
	def Test3vOnePublished2Definition()
	{
		when:
			ApprendaClient.NewApplication(uc6props, 'v1')
			ApprendaClient.PatchApplication(uc6props, 'v1')
			ApprendaClient.Promote(uc6props, 'v1')
			ApprendaClient.Promote(uc6props, 'v1')
			ApprendaClient.PostNewVersion(uc6props, 'v2')
			ApprendaClient.PatchApplication(uc6props, 'v2')
			ApprendaClient.PostNewVersion(uc6props, 'v3')
			ApprendaClient.PatchApplication(uc6props, 'v3')
			def uc6data = ApprendaClient.GetApplicationInfo(uc6props)
			def uc6vd = new InternalDeployApp().detectVersion(uc6data, uc6props)
		then:
			uc6vd.newVersionRequired == false
			uc6vd.targetVersion == 'v3'
	}
	
	def Test3vOneOfEach()
	{
		when:
			ApprendaClient.NewApplication(uc7props, 'v1')
			ApprendaClient.PatchApplication(uc7props, 'v1')
			ApprendaClient.Promote(uc7props, 'v1')
			ApprendaClient.Promote(uc7props, 'v1')
			ApprendaClient.PostNewVersion(uc7props, 'v2')
			ApprendaClient.PatchApplication(uc7props, 'v2')
			ApprendaClient.Promote(uc7props, 'v2')
			ApprendaClient.PostNewVersion(uc7props, 'v3')
			def uc7data = ApprendaClient.GetApplicationInfo(uc7props)
			def uc7vd = new InternalDeployApp().detectVersion(uc7data, uc7props)
		then:
			uc7vd.newVersionRequired == false
			uc7vd.targetVersion == 'v3'
	}
	
	def Test3vOnePublished2Sandbox()
	{
		when:
			ApprendaClient.NewApplication(uc8props, 'v1')
			ApprendaClient.PatchApplication(uc8props, 'v1')
			ApprendaClient.Promote(uc8props, 'v1')
			ApprendaClient.Promote(uc8props, 'v1')
			ApprendaClient.PostNewVersion(uc8props, 'v2')
			ApprendaClient.PatchApplication(uc8props, 'v2')
			ApprendaClient.Promote(uc8props, 'v2')
			ApprendaClient.PostNewVersion(uc8props, 'v3')
			ApprendaClient.PatchApplication(uc8props, 'v3')
			ApprendaClient.Promote(uc8props, 'v3')
			def uc8data = ApprendaClient.GetApplicationInfo(uc8props)
			def uc8vd = new InternalDeployApp().detectVersion(uc8data, uc8props)
		then:
			uc8vd.newVersionRequired == false
			uc8vd.targetVersion == 'v3'
	}
	
	def TestFailureCase1()
	{
		// tests when we get bad data from Apprenda
		setup:
			def failure1 = ["aosdinsaf"]
		when:
			new InternalDeployApp().detectVersion(failure1, uc1props)
		then:
			thrown Exception	
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
