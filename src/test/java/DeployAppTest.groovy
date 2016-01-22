package test.java
import spock.lang.Ignore
import spock.lang.IgnoreRest;
import spock.lang.Shared
import spock.lang.Specification
import main.java.*
import groovy.util.logging.Slf4j

@Slf4j
public class DeployAppTest extends Specification {
	 
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
			'Stage':props["ACTProperties.Stage"]	
			]
	}
	
	// I use this for bypassing all of the tests in a given class during maven test runs. 
	@IgnoreRest
	def Bypass()
	{
		expect:
			1 == 1
	}
	
	def Test1vDefinition()
	{
		setup:
			log.info("Starting Test Case 1")
			def instanceTest = testProperties
			instanceTest.AppAlias = 'Test1vDefinition'
			def newApp = ApprendaClient.NewApplication(instanceTest)
			def patchApp = ApprendaClient.PatchApplication(instanceTest, 'v1')
			def uc1data = ApprendaClient.GetApplicationInfo(instanceTest)
			def uc1vd = new InternalDeployApp().detectVersion(uc1data.getData(), instanceTest)
		expect:
			uc1vd.newVersionRequired == false
			uc1vd.targetVersion == 'v1'
		cleanup:
			ApprendaClient.DeleteApplication(instanceTest)
	}
	
	def Test1vSandbox()
	{
		setup:
			log.info("Starting Test Case 2")
			def instanceTest = testProperties
			instanceTest.AppAlias = 'Test1vSandbox'
			ApprendaClient.NewApplication(instanceTest)
			ApprendaClient.PatchApplication(instanceTest, 'v1')
			ApprendaClient.Promote(instanceTest, 'v1')
			def uc2data = ApprendaClient.GetApplicationInfo(instanceTest)
			def uc2vd = new InternalDeployApp().detectVersion(uc2data.getData(), instanceTest)
		expect:
			uc2vd.newVersionRequired == false
			uc2vd.targetVersion == 'v1'
		cleanup:
			ApprendaClient.DeleteApplication(instanceTest)
	}
	
	def Test1vPublished()
	{
		setup:
			log.info("Starting Test Case 3")
			def instanceTest = testProperties
			instanceTest.AppAlias = 'Test1vPublished'
			instanceTest.Stage = null
			ApprendaClient.NewApplication(instanceTest)
			ApprendaClient.PatchApplication(instanceTest, 'v1')
			def testPromoteSandbox = ApprendaClient.Promote(instanceTest, 'v1')
			log.info(testPromoteSandbox.getData().toString())
			def testPromotePublished = ApprendaClient.Promote(instanceTest, 'v1')
			log.info(testPromotePublished.getData().toString())
			def uc3data = ApprendaClient.GetApplicationInfo(instanceTest)
			def uc3vd = new InternalDeployApp().detectVersion(uc3data.getData(), instanceTest)
		expect:
			uc3vd.newVersionRequired == true
			uc3vd.targetVersion == 'v2'
		cleanup:
			ApprendaClient.DeleteApplication(instanceTest)
	}
	
	def Test2vOnePublishedOneDefinition()
	{
		setup:
			log.info("Starting Test Case 4")
			def instanceTest = testProperties
			instanceTest.AppAlias = 'Test2vOnePubOneDef'
			def api1 = ApprendaClient.NewApplication(instanceTest)
			def api2 = ApprendaClient.PatchApplication(instanceTest, 'v1')
			ApprendaClient.Promote(instanceTest, 'v1')
			ApprendaClient.Promote(instanceTest, 'v1')
			ApprendaClient.PostNewVersion(instanceTest, 'v2')
			ApprendaClient.PatchApplication(instanceTest, 'v2')
			def uc4data = ApprendaClient.GetApplicationInfo(instanceTest)
			def uc4vd = new InternalDeployApp().detectVersion(uc4data.getData(), instanceTest)
		expect:
			uc4vd.newVersionRequired == false
			uc4vd.targetVersion == 'v2'
		cleanup:
			ApprendaClient.DeleteApplication(instanceTest)
	}
	
	def Test2vOnePublishedOneSandbox()
	{
		setup:
			log.info("Starting Test Case 5")
			def instanceTest = testProperties
			instanceTest.AppAlias = 'Test2vOnePubOneSand'
			instanceTest.Stage = 'Published'
			ApprendaClient.NewApplication(instanceTest)
			ApprendaClient.PatchApplication(instanceTest, 'v1')
			ApprendaClient.Promote(instanceTest, 'v1')
			instanceTest.Stage = null
			ApprendaClient.PostNewVersion(instanceTest, 'v2')
			ApprendaClient.PatchApplication(instanceTest, 'v2')
			// can't blind promote with v2, need more info.
			instanceTest.Stage = 'Sandbox'
			ApprendaClient.Promote(instanceTest, 'v2')
			def uc5data = ApprendaClient.GetApplicationInfo(instanceTest)
			def uc5vd = new InternalDeployApp().detectVersion(uc5data.getData(), instanceTest)
		expect:
			uc5vd.newVersionRequired == false
			uc5vd.targetVersion == 'v2'
		cleanup:
			ApprendaClient.DeleteApplication(instanceTest)
	}
	
	def Test3vOnePublished2Definition()
	{
		setup:
			log.info("Starting Test Case 6")
			def instanceTest = testProperties
			instanceTest.AppAlias = 'Test3vOnePub2Def'
			ApprendaClient.NewApplication(instanceTest)
			ApprendaClient.PatchApplication(instanceTest, 'v1')
			instanceTest.Stage = 'Published'
			def promote = ApprendaClient.Promote(instanceTest, 'v1')
			log.info("promoteTest: " + promote.getData().toString())
			ApprendaClient.PostNewVersion(instanceTest, 'v2')
			ApprendaClient.PatchApplication(instanceTest, 'v2')
			ApprendaClient.PostNewVersion(instanceTest, 'v3')
			ApprendaClient.PatchApplication(instanceTest, 'v3')
			def uc6data = ApprendaClient.GetApplicationInfo(instanceTest)
			log.info("appInfo: " + uc6data.getData().toString())
			def uc6vd = new InternalDeployApp().detectVersion(uc6data.getData(), instanceTest)
		expect:
			uc6vd.newVersionRequired == false
			uc6vd.targetVersion == 'v3'
		cleanup:
			ApprendaClient.DeleteApplication(instanceTest)
	}
	
	def Test3vOneOfEach()
	{
		setup:
			log.info("Starting Test Case 7")
			def instanceTest = testProperties
			instanceTest.AppAlias = 'Test3vOneOfEach'
			ApprendaClient.NewApplication(instanceTest)
			ApprendaClient.PatchApplication(instanceTest, 'v1')
			ApprendaClient.Promote(instanceTest, 'v1')
			ApprendaClient.Promote(instanceTest, 'v1')
			ApprendaClient.PostNewVersion(instanceTest, 'v2')
			ApprendaClient.PatchApplication(instanceTest, 'v2')
			// promote v2 to sandbox
			instanceTest.Stage = 'Sandbox'
			ApprendaClient.Promote(instanceTest, 'v2')
			// put v3 in definition
			instanceTest.Stage = 'Definition'
			ApprendaClient.PostNewVersion(instanceTest, 'v3')
			ApprendaClient.PatchApplication(instanceTest, 'v3')
			def uc7data = ApprendaClient.GetApplicationInfo(instanceTest)
			def uc7vd = new InternalDeployApp().detectVersion(uc7data.getData(), instanceTest)
		expect:
			uc7vd.newVersionRequired == false
			uc7vd.targetVersion == 'v3'
		cleanup:
			ApprendaClient.DeleteApplication(instanceTest)
	}
	
	def Test3vOnePublished2Sandbox()
	{
		setup:
			log.info("Starting Test Case 8")
			def instanceTest = testProperties
			instanceTest.AppAlias = 'Test3vOnePub2Sand'
			ApprendaClient.NewApplication(instanceTest)
			ApprendaClient.PatchApplication(instanceTest, 'v1')
			instanceTest.Stage = 'Published'
			ApprendaClient.Promote(instanceTest, 'v1')
			ApprendaClient.PostNewVersion(instanceTest, 'v2')
			ApprendaClient.PatchApplication(instanceTest, 'v2')
			// change for v2 and v3
			instanceTest.Stage = 'Sandbox'
			ApprendaClient.Promote(instanceTest, 'v2')
			ApprendaClient.PostNewVersion(instanceTest, 'v3')
			ApprendaClient.PatchApplication(instanceTest, 'v3')
			ApprendaClient.Promote(instanceTest, 'v3')
			def uc8data = ApprendaClient.GetApplicationInfo(instanceTest)
			def uc8vd = new InternalDeployApp().detectVersion(uc8data.getData(), instanceTest)
		expect:
			uc8vd.newVersionRequired == false
			uc8vd.targetVersion == 'v3'
		cleanup:
			ApprendaClient.DeleteApplication(instanceTest)
	}	
}