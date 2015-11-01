package com.apprenda.integrations.urbancode.test
import spock.lang.Specification
import com.apprenda.integrations.urbancode.*
import com.apprenda.integrations.urbancode.util.*

public class DeployAppTests extends Specification {
	
	def testProperties = [ApprendaURL:'https://apps.apprenda.heineken',
		ApprendaUser:'fluffy@apprenda.com',
		ApprendaPassword:'password',
		TenantAlias:'warkittens',
		SelfSignedFlag:true,
		AppAlias:'apprendazon']
	
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
	
	// 
	def DeployAppTestSandbox() {
		setup: 
			def app = ApprendaClient.GetApplicationInfo(testProperties)
		
	}
	
	def DeployAppTestPublished() {
		
	}

}
