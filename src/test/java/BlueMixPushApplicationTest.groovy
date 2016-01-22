package test.java;
import spock.lang.Specification

import spock.lang.Ignore
import spock.lang.Shared;
import main.java.*
public class BlueMixPushApplicationTest extends Specification {

	
	@Shared TestProperties = [:]
	
	def setupSpec()
	{
		def props = new Properties()
		new File("src/test/resources/testing.properties").withInputStream {
			stream -> props.load(stream)
		}
		testProperties = [
			'api':props["Bluemix.api"],
			'user':props["Bluemix.user"],
			'password':props["Bluemix.password"],
			'selfsigned':props["Bluemix.selfsigned"],
			'org':props["Bluemix.org"],
			'space':props["Bluemix.space"],
			'application':props["Bluemix.application"],
			'ApprendaArchiveLocation':props["Bluemix.ApprendaArchiveLocation"],
			'url':props["Bluemix.url"],
			'alias':props["Bluemix.alias"]
			]
	}
	
	@Ignore
	def TestDeployment()
	{
		setup:
				def internal = new InternalPushApplication()
				internal.deployAppToBlueMix(testProperties)
				
		expect:
				1==1
	}
}
