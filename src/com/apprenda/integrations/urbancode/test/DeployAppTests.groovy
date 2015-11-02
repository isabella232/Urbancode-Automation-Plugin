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
	
	
	// these tests are more functional than anything else but we should run these
	// as smoke tests
	def DeployAppTestSandbox() {
		setup: 
			def app = ApprendaClient.GetApplicationInfo(testProperties)
	}
	
	def DeployAppTestPublished() {
		
	}
	
	def DeployAppTwoVersions(){
	
	}
}
