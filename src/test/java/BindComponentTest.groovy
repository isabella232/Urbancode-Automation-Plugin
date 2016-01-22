package test.java
import spock.lang.IgnoreRest
import spock.lang.Shared;
import spock.lang.Specification
import com.apprenda.integrations.urbancode.*
import org.custommonkey.xmlunit.*
import spock.lang.Ignore
import com.apprenda.integrations.urbancode.util.*
import main.java.InternalBindBluemixComponentToApprendaApp
import groovy.util.logging.Slf4j
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption;
import java.nio.file.Files

@Slf4j
class BindComponentTest extends Specification {

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
	
	def TestRetrievalOfURL()
	{
		setup:
			def internal = new InternalBindBluemixComponentToApprendaApp()
			def appinfo = internal.GetBluemixComponentInfo(testProperties)
			log.info('appinfo: ' + appinfo)
		expect:
			appinfo.state.equals('started')
			appinfo.urls.equals(testProperties.url)
	}
	
	
	def TestUpdateConfigFiles()
	{
		setup:
			def internal = new InternalBindBluemixComponentToApprendaApp()
			def testReturnCode = internal.UpdateConfigurationFiles(testProperties)
		expect:
			testReturnCode == 0
	}
	
	
	def TestUpdateWebXmlFile()
	{
		setup:
			def internal = new InternalBindBluemixComponentToApprendaApp()
			// to be safe, make a backup of the original file
			Path original = Paths.get("src/test/resources/configFileTest/JavaApp/web.xml")
			Path copy = Paths.get("src/test/resources/configFileTest/JavaApp/web.xml.copy")
			Files.copy(copy, original, StandardCopyOption.REPLACE_EXISTING)
			// this should overwrite the file
			internal.CheckAndReplaceTokens(original.toFile(), testProperties)
			def parser = new XmlParser()
			parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
			parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			def xml = parser.parse(original.toFile())
			def xml2 = parser.parse(copy.toFile())
			def areaToTest = xml.children().findAll { it.name() == 'context-param' }
			def areaToTestAgainst = xml2.children().findAll { it.name() == 'context-param'}
		expect:
			// context-params in the original and subsituted file should be different
			areaToTest.toString() != areaToTestAgainst.toString()
	}
	
	def TestUpdateDotNetFile()
	{
		setup:
			def internal = new InternalBindBluemixComponentToApprendaApp()
			// to be safe, make a backup of the original file
			Path original = Paths.get("src/test/resources/configFileTest/DotNetApp/web.config")
			Path copy = Paths.get("src/test/resources/configFileTest/DotNetApp/web.config.copy")
			Path apporiginal = Paths.get("src/test/resources/configFileTest/DotNetApp/app.config")
			Path appcopy = Paths.get("src/test/resources/configFileTest/DotNetApp/app.config.copy")
			Files.copy(copy, original, StandardCopyOption.REPLACE_EXISTING)
			// this should overwrite the file
			internal.CheckAndReplaceTokens(original.toFile(), testProperties)
			internal.CheckAndReplaceTokens(apporiginal.toFile(), testProperties)
			def parser = new XmlParser()
			parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
			parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			def xml = parser.parse(original.toFile())
			def xml2 = parser.parse(copy.toFile())
			def xml3 = parser.parse(apporiginal.toFile())
			def xml4 = parser.parse(appcopy.toFile())
			def areaToTest = xml.children().findAll { it.name() == 'appSettings' }
			def areaToTestAgainst = xml2.children().findAll { it.name() == 'appSettings'}
			def areaToTest2 = xml3.children().findAll { it.name() == 'appSettings' }
			def areaToTestAgainst2 = xml4.children().findAll { it.name() == 'appSettings'}
		expect:
			areaToTest.toString() != areaToTestAgainst.toString()
			areaToTest2.toString() != areaToTestAgainst2.toString()
	}
	
	def TestUpdateDockerFile()
	{
		setup:
		def internal = new InternalBindBluemixComponentToApprendaApp()
		// to be safe, make a backup of the original file
		Path original = Paths.get("src/test/resources/configFileTest/DockerContainer/Dockerfile")
		Path copy = Paths.get("src/test/resources/configFileTest/DockerContainer/Dockerfile.copy")
		Files.copy(copy, original, StandardCopyOption.REPLACE_EXISTING)
		// this should overwrite the file
		internal.CheckAndReplaceTokens(original.toFile(), testProperties)
	expect:
		// expect that the modified dockerfile has an extra line
		original.toFile().readLines().size() > copy.toFile().readLines().size()
	}
}
