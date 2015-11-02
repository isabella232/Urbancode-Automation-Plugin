package com.apprenda.integrations.urbancode.test
import spock.lang.Specification
import com.apprenda.integrations.urbancode.*
import com.apprenda.integrations.urbancode.util.*

class ApprendaClientTests extends Specification {
	def testProperties = [ApprendaURL:'https://apps.apprenda.heineken',
		ApprendaUser:'fluffy@apprenda.com',
		ApprendaPassword:'password',
		TenantAlias:'warkittens',
		SelfSignedFlag:true,
		AppAlias:'apprendazon',
		ArchiveLocation:'testapps/apprendazon-1.0.zip',
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
	
	def TestGetApplicationInfo()
	{
		setup:
			def data = ApprendaClient.GetApplicationInfo(testProperties)
		expect:
			data.alias == 'apprendazon'
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
			def response = ApprendaClient.NewApplication(newAppProperties, 'v1')
			def response2 = ApprendaClient.NewApplication(newAppProperties, 'v1')
			def data = ApprendaClient.GetApplicationInfo(newAppProperties)
		expect:
			response.status == 201
			// shouldn't be able to create the same app twice
			response2.status == 409
			data.alias == 'newapplication'
		cleanup:
			def deleteResponse = ApprendaClient.DeleteApplication(newAppProperties)
	}
	
	def TestDeleteApplication()
	{
		setup:
			def newAppProperties = testProperties
			newAppProperties.AppAlias = 'apptobedeleted'
			def newApp = ApprendaClient.NewApplication(newAppProperties, 'v1')
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
			def response = ApprendaClient.NewApplication(patchAppProperties, 'v1')
			def patch = ApprendaClient.PatchApplication(patchAppProperties, 'v1')
		expect:
			response.status == 201
			patch.status == 200
		cleanup:
			def deleteResponse = ApprendaClient.DeleteApplication(patchAppProperties)
	}
	
	def TestPromoteDemoteApplication()
	{
		setup:
			def promoteAppProperties = testProperties
			promoteAppProperties.AppAlias = 'promoteApplication'
			def response = ApprendaClient.NewApplication(promoteAppProperties, 'v1')
			def patch = ApprendaClient.PatchApplication(promoteAppProperties, 'v1')
			def promote = ApprendaClient.Promote(promoteAppProperties, 'v1')
			def data1 = ApprendaClient.GetApplicationInfo(promoteAppProperties)
			def demote = ApprendaClient.Demote(promoteAppProperties, 'v1')
			def data2 = ApprendaClient.GetApplicationInfo(promoteAppProperties)
		expect:
			response.status == 201
			promote.status == 200
			demote.status == 200
		cleanup:
			def deleteResponse = ApprendaClient.DeleteApplication(promoteAppProperties)
	}

	def TestPostNewVersionApplication()
	{
		setup:
			def promoteAppProperties = testProperties
			promoteAppProperties.AppAlias = 'promoteApplication'
			def response = ApprendaClient.NewApplication(promoteAppProperties, 'v1')
			def patch = ApprendaClient.PatchApplication(promoteAppProperties, 'v1')
			def promote = ApprendaClient.Promote(promoteAppProperties, 'v1')
			def data1 = ApprendaClient.GetApplicationInfo(promoteAppProperties)
			def promote2 = ApprendaClient.Promote(promoteAppProperties, 'v1')
			def data2 = ApprendaClient.GetApplicationInfo(promoteAppProperties)
			def newVersion = ApprendaClient.PostNewVersion(promoteAppProperties, 'v2')
			def patch2 = ApprendaClient.PatchApplication(promoteAppProperties, 'v2')
			def promote3 = ApprendaClient.Promote(promoteAppProperties, 'v2')
			def data3 = ApprendaClient.GetApplicationInfo(promoteAppProperties)
			def promote4 = ApprendaClient.Promote(promoteAppProperties, 'v2')
			def data4 = ApprendaClient.GetApplicationInfo(promoteAppProperties)
		expect:
			response.status == 201
			promote.status == 200
			promote2.status == 200
			newVersion.status == 201
			promote3.status == 200
			promote4.status == 200
			// we'll do tests with the data later.
	}
}
