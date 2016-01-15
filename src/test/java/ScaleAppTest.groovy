package test.java
import groovy.util.logging.Slf4j
import spock.lang.Ignore
import spock.lang.Specification
import main.java.*

@Slf4j
class ScaleAppTest extends Specification {
	
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
	
	@Ignore
	def TestInstanceScaleUp()
	{
		setup:
			def promoteAppProperties = testProperties
			promoteAppProperties.AppAlias = 'scaleApp'
			def response = ApprendaClient.NewApplication(promoteAppProperties)
			//def patch = ApprendaClient.PatchApplication(promoteAppProperties, 'v1')
			def promote = ApprendaClient.Promote(promoteAppProperties)
			//def data1 = ApprendaClient.GetApplicationInfo(promoteAppProperties)
			def promote2 = ApprendaClient.Promote(promoteAppProperties)
			def scaleApp = ApprendaClient.SetInstanceCount(promoteAppProperties)
		expect:
			response.status == 201
			promote.status == 200
			promote2.status == 200
			scaleApp.status == 204
		cleanup:
			ApprendaClient.DeleteApplication(promoteAppProperties)
	}

	
}
