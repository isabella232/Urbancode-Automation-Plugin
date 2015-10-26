/**
 * 
 */
package com.apprenda.integrations.urbancode
/**
 * @author cdutra
 *
 */
import com.urbancode.air.AirPluginTool
import static groovyx.net.http.ContentType.*
final def apTool = new AirPluginTool(this.args[0], this.args[1])
final def props = apTool.getStepProperties()

// the goal of this step is to inject custom properties that are apprenda-aware

// determine whether the custom property being used is at the 
// application level or the component level
final def isApplicationLevelCP = props.isApplicationLevelCP

try
{
	// load deployment manifest file
	def xmlFile = "DeploymentManifest.xml"
	def xml = new XMLParser().parse(xmlFile)
	// test if parent node exists, if not create it.
	
	
}
catch(FileNotFoundException e)
{
	println "Could not load deployment manifest file, check to make sure your build includes this file."
	e.printStackTrace()
	System.exit 1
}

