package test.java
import spock.lang.IgnoreRest;
import spock.lang.Specification
import com.apprenda.integrations.urbancode.*
import org.custommonkey.xmlunit.*
import groovy.util.XmlSlurper
import com.apprenda.integrations.urbancode.util.*
import com.sun.org.apache.xerces.internal.util.XML11Char
import groovy.xml.XmlUtil
import main.java.InternalBindBluemixComponentToApprendaApp;
class BindComponentTests extends Specification {

	def props = [
					api:'https://api.ng.bluemix.net',
					user:'cdutra@apprenda.com',
					password:'Meepster23',
					selfsigned:false,
					org:'cdutra@apprenda.com',
					space:'dev',
					application:'dutronflask',
					ApprendaArchiveLocation: 'testApps/configFileTest',
					url:'dutronflask.mybluemix.net',
					alias: 'bluemixflask'
				]
	
	
	def TestRetrievalOfURL()
	{
		setup:
			def internal = new InternalBindBluemixComponentToApprendaApp()
			def appinfo = internal.GetBluemixComponentInfo(props)
			println 'appinfo: ' + appinfo
		expect:
			appinfo.state.equals('started')
			appinfo.urls.equals('dutronflask.mybluemix.net')
	}
	
	@IgnoreRest
	def TestUpdateConfigFiles()
	{
		setup:
			def internal = new InternalBindBluemixComponentToApprendaApp()
			def testReturnCode = internal.UpdateConfigurationFiles(props)
		expect:
			testReturnCode == 0
	}
}
