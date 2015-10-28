package com.apprenda.integrations.urbancode.test
import spock.lang.Specification
import com.apprenda.integrations.urbancode.*

public class DeployAppTests extends Specification {
	
	// test case 1: deploy 
	def DeployAppTestDefinition() {
		expect:
			name.size() == length
			
		where:
			name << ["Kirk", "Spock"]
			length << [4,5]
			
	}
	
	// creating tests to 
	def DeployAppTestSandbox() {
		
	}
	
	def DeployAppTestPublished() {
		
	}

}
