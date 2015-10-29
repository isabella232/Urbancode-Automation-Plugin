package com.apprenda.integrations.urbancode.test
import spock.lang.Specification
import com.apprenda.integrations.urbancode.*

public class DeployAppTests extends Specification {
	
	def testProperties = [ApprendaURL:'https://apps.apprenda.heineken',
		ApprendaUser:'cdutra@apprenda.com',
		ApprendaPassword:'Meepster23!',
		TenantAlias:'dutronlabs',
		SelfSignedFlag:true,
		AppAlias:'test']
	
	def TestTheTestValidationRoutine()
	{
		setup:
			def test = TestValidation.getInstance(testProperties)
		expect:
			test.defaultRequestHeaders != null
			test.defaultRequestHeaders.'ApprendaSessionToken' != null
	}
	
	def TestDataRetrieval()
	{
		setup:
			def data = TestValidation.GetApplicationInfo(testProperties)
		expect:
			data.alias == 'test'
	}
	
	// 
	def DeployAppTestSandbox() {
		
	}
	
	def DeployAppTestPublished() {
		
	}

}
