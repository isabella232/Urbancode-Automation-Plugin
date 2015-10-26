package com.apprenda.integrations.urbancode.test
import spock.lang.Specification

public class DeployAppTests extends Specification {
	
	def spockTest() {
		expect:
			name.size() == length
			
		where:
			name << ["Kirk", "Spock"]
			length << [4,5]
			
	}
}
