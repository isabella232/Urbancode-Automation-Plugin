package test.java
import groovy.util.logging.Slf4j
import spock.lang.Ignore
import spock.lang.IgnoreRest
import spock.lang.Shared;
import spock.lang.Specification
import main.java.*

@Slf4j
class ScaleAppTest extends Specification {
	
	// Useful for testing different configurations. Note that for now we can only run tests on one environment at a time.
	@Shared TestProperties = [:]

	def setupSpec()
	{
		def props = new Properties()
		new File("src/test/resources/testing.properties").withInputStream {
			stream -> props.load(stream)
		}
		testProperties = [
			'ApprendaURL':props["ACTProperties.ApprendaURL"],
			'ApprendaUser':props["ACTProperties.ApprendaUser"],
			'ApprendaPassword':props["ACTProperties.ApprendaPassword"],
			'TenantAlias':props["ACTProperties.TenantAlias"],
			'SelfSignedFlag':props["ACTProperties.SelfSignedFlag"],
			'AppAlias':props["ACTProperties.AppAlias"],
			'ArchiveName':props["ACTProperties.ArchiveName"],
			'Stage':props["ACTProperties.Stage"],
			'ComponentAlias':props["ACTProperties.ComponentAliasWeb"]
			]
	}
	
	@IgnoreRest
	def bypass()
	{
		expect:
			1==1
	}
	
	def TestInstanceScale()
	{
		setup:
			def promoteAppProperties = testProperties
			promoteAppProperties.AppAlias = 'scaleApp'
			ApprendaClient.NewApplication(promoteAppProperties)
			ApprendaClient.PatchApplication(promoteAppProperties, 'v1')
			// promote to Published
			promoteAppProperties.Stage = 'Published'
			ApprendaClient.Promote(promoteAppProperties, 'v1')
			// scale it up!
			testProperties.InstanceCount = 3
			def scaleApp = ApprendaClient.SetInstanceCount(promoteAppProperties, 'v1')
			log.info(scaleApp.getData().toString())
			// get instance count
			def appInfo = ApprendaClient.GetApplicationInfo(promoteAppProperties)
			log.info(appInfo.getData().toString())
			def componentInfo = ApprendaClient.GetComponentInfo(promoteAppProperties, 'v1')
			log.info(componentInfo.getData().toString())
		expect:
			scaleApp.status == 204
			testProperties.InstanceCount == componentInfo.getData().instances.count
		cleanup:
			ApprendaClient.DeleteApplication(promoteAppProperties)
	}

	
}
