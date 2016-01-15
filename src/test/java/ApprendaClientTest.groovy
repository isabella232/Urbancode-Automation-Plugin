package test.java
import spock.lang.IgnoreRest
import spock.lang.Specification
import groovy.util.logging.Slf4j
import main.java.ApprendaClient

@Slf4j
class ApprendaClientTest extends Specification {
	def testProperties = [ApprendaURL:'https://apps.apprenda.heineken',
		ApprendaUser:'fluffy@apprenda.com',
		ApprendaPassword:'password',
		TenantAlias:'warkittens',
		SelfSignedFlag:true,
		AppAlias:'apprendazon',
		ArchiveName:'testapps/TimeCard.zip',
		Stage:'definition']
	
	// First tests relate to make sure our Apprenda Client is working properly.
	def TestApprendaClientInstantiation()
	{
		setup:
			def test = ApprendaClient.getInstance(testProperties)
		expect:
			test.defaultRequestHeaders != null
			test.defaultRequestHeaders.'ApprendaSessionToken' != null
	}
	
	// need to rewrite this test as we can't guarantee an app alias is going to be there. need to create an app first.
	def TestGetApplicationInfo()
	{
		setup:
			ApprendaClient.NewApplication(testProperties)
			def data = ApprendaClient.GetApplicationInfo(testProperties)
		expect:
			data.getData().alias == 'apprendazon'
	}
	
	def TestBadGetApplicationInfo()
	{
		setup:
			testProperties.AppAlias = 'aoisnfsaod'
			def data = ApprendaClient.GetApplicationInfo(testProperties)
		expect:
			data.status == 404
	}
	
	def TestGetVersionInfo()
	{
		setup:
			def data = ApprendaClient.GetVersionInfo(testProperties)
		expect:
			data != null
	}
	
	def TestNewApplication()
	{
		// test new app creation and then test to make sure app got created
		setup:
			def newAppProperties = testProperties
			newAppProperties.AppAlias = 'newapplication'
			def response = ApprendaClient.NewApplication(newAppProperties)
			def response2 = ApprendaClient.NewApplication(newAppProperties)
			def data = ApprendaClient.GetApplicationInfo(newAppProperties)
		expect:
			response.status == 201
			// shouldn't be able to create the same app twice
			response2.status == 409
			data.getData().alias == 'newapplication'
		cleanup:
			ApprendaClient.DeleteApplication(newAppProperties)
	}
	
	def TestDeleteApplication()
	{
		setup:
			def newAppProperties = testProperties
			newAppProperties.AppAlias = 'apptobedeleted'
			def newApp = ApprendaClient.NewApplication(newAppProperties)
			def data = ApprendaClient.GetApplicationInfo(newAppProperties)
			def deleteApp = ApprendaClient.DeleteApplication(newAppProperties)
			def data2 = ApprendaClient.GetApplicationInfo(newAppProperties)
		expect:
			newApp.status == 201
			deleteApp.status == 204
			data != data2
	}

	def TestPatchApplication()
	{
		setup:
			def patchAppProperties = testProperties
			patchAppProperties.AppAlias = 'patchApplication'
			def response = ApprendaClient.NewApplication(patchAppProperties)
			def patch = ApprendaClient.PatchApplication(patchAppProperties, 'v1')
		expect:
			response.status == 201
			patch.status == 200
		cleanup:
			ApprendaClient.DeleteApplication(patchAppProperties)
	}
	
	def TestPromoteDemoteApplication()
	{
		setup:
			def promoteAppProperties = testProperties
			promoteAppProperties.AppAlias = 'promoteApplication'
			def response = ApprendaClient.NewApplication(promoteAppProperties)
			ApprendaClient.PatchApplication(promoteAppProperties, 'v1')
			// badPromote: try to "promote" to Definition
			def badPromote = ApprendaClient.Promote(promoteAppProperties, 'v1')
			// promote to Sandbox
			promoteAppProperties.Stage = 'sandbox'
			def promote = ApprendaClient.Promote(promoteAppProperties, 'v1')
			log.info(promote.status.toString())
			log.info(promote.getData().toString())
			// demote it back to definition
			def demote = ApprendaClient.Demote(promoteAppProperties, 'v1')
			log.info(demote.status.toString())
			log.info(demote.getData().toString())
			// and then test the promotion to published from definition
			promoteAppProperties.Stage = 'published'
			def promote2 = ApprendaClient.Promote(promoteAppProperties, 'v1')
			// and then "try to demote" but it shouldn't
			def badDemote = ApprendaClient.Demote(promoteAppProperties, 'v1')
		expect:
			response.status == 201
			badPromote.status == 400
			promote.status == 200
			demote.status == 200
			promote2.status == 200
			badDemote.status == 400
		cleanup:
			ApprendaClient.DeleteApplication(promoteAppProperties)
	}

	def TestBlindPromote()
	{
		setup:
			def promoteAppProperties = testProperties
			promoteAppProperties.AppAlias = 'noStageApp'
			def response = ApprendaClient.NewApplication(promoteAppProperties)
			ApprendaClient.PatchApplication(promoteAppProperties, 'v1')
			// test a blind promote from definition to sandbox
			promoteAppProperties.Stage = null
			def promote = ApprendaClient.Promote(promoteAppProperties, 'v1')
			// this is insane. the property changes are persisted in the method later.
			promoteAppProperties.Stage = null
			// test a blind promote from sandbox to published
			def promote2 = ApprendaClient.Promote(promoteAppProperties, 'v1')
		expect:
			response.status == 201
			promote.status == 200
			promote2.status == 200
		cleanup:
			ApprendaClient.DeleteApplication(promoteAppProperties)
	}

	def TestPostNewVersionApplication()
	{
		setup:
			def promoteAppProperties = testProperties
			promoteAppProperties.AppAlias = 'promoteApplication'
			def response = ApprendaClient.NewApplication(promoteAppProperties)
			ApprendaClient.PatchApplication(promoteAppProperties, 'v1')
			promoteAppProperties.Stage = 'Published'
			def promote = ApprendaClient.Promote(promoteAppProperties, 'v1')
			log.info(promote.getData().toString())
			def newVersion = ApprendaClient.PostNewVersion(promoteAppProperties, 'v2')
			def patch = ApprendaClient.PatchApplication(promoteAppProperties, 'v2')
		expect:
			response.status == 201
			promote.status == 200
			newVersion.status == 201
			patch.status == 200
		cleanup:
			ApprendaClient.DeleteApplication(promoteAppProperties)
	}
}
