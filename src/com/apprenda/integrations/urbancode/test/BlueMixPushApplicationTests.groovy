package com.apprenda.integrations.urbancode.test;
import spock.lang.Specification
import com.apprenda.integrations.urbancode.*
import com.apprenda.integrations.urbancode.util.*

public class BlueMixPushApplicationTests extends Specification {
	def testProps = [api:'https://api.ng.bluemix.net',
		appName:'dutronflask',
		buildpack:'https://github.com/cloudfoundry/python-buildpack',
		disk:'1G',
		domain:null,
		instances:1,
		manifest:null,
		memory:'128M',
		nohostname:false,
		nomanifest:false,
		noroute:false,
		nostart:false,
		org:'cdutra@apprenda.com',
		path:'testapps/dutronflask',
		randomroute:false,
		selfSigned:false,
		space:'dev',
		stack:'cflinuxfs2',
		subdomain:null,
		timeout:null,
		user:'cdutra@apprenda.com',
		password:'Meepster23']
	
	def TestDeployment()
	{
		setup:
				final def workDir = new File('.').canonicalFile
				def internal = new InternalPushApplication()
		when:
				internal.deployAppToBlueMix(testProps)
		then:
				notThrown Exception
	}
}
