package test.java
import spock.lang.Shared
import spock.lang.Specification
import main.java.*
class ScaleAppTests extends Specification {
	
	def testProperties = [ApprendaURL:'https://apps.apprenda.heineken',
		ApprendaUser:'fluffy@apprenda.com',
		ApprendaPassword:'password',
		TenantAlias:'warkittens',
		SelfSignedFlag:true,
		AppAlias:'apprendazon',
		ArchiveLocation:'testapps/apprendazon-1.0.zip',
		Stage:'definition',
		ComponentAlias:'ui-root',
		InstanceCount:3]
	
	def TestInstanceScaleUp()
	{
		setup:
			def promoteAppProperties = testProperties
			promoteAppProperties.AppAlias = 'scaleApp'
			def response = ApprendaClient.NewApplication(promoteAppProperties, 'v1')
			def patch = ApprendaClient.PatchApplication(promoteAppProperties, 'v1')
			def promote = ApprendaClient.Promote(promoteAppProperties, 'v1')
			def data1 = ApprendaClient.GetApplicationInfo(promoteAppProperties)
			def promote2 = ApprendaClient.Promote(promoteAppProperties, 'v1')
			def scaleApp = ApprendaClient.SetInstanceCount(promoteAppProperties, 'v1')
		expect:
			response.status == 201
			promote.status == 200
			promote2.status == 200
			scaleApp.status == 204
		cleanup:
			def deleteResponse = ApprendaClient.DeleteApplication(promoteAppProperties)
	}

	
}
