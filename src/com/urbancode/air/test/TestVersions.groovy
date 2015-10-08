package com.urbancode.air.test

	def testSimple = [AppAlias:'taskr1', versions:[[alias:'v1', stage:'Sandbox']]]
	def testPublished = [AppAlias:'taskr1', versions:[[alias:'v1', stage:'Published']]]
	def testPublishedwv2 = [AppAlias:'taskr1', versions:[[alias:'v1', stage:'Published'], [alias:'v2', stage:'Sandbox']]]
	def testPublishedwv2v3 = [AppAlias:'taskr1', versions:[[alias:'v1', stage:'Published'], [alias:'v2', stage:'Sandbox'], [alias:'v3', stage:'Sandbox']]]
	def tests = [testSimple, testPublished, testPublishedwv2, testPublishedwv2v3]
	def testSimpleResults = [newVersionRequiredTest:false, newVersionAlias:'v1', highestVersionStage:'Sandbox', highestVersionFound:'v1']
	def testPublishedResults = [newVersionRequiredTest:true, newVersionAlias:'v2', highestVersionStage:'Published', highestVersionFound:'v1']
	def testPublishedwv2Results = [newVersionRequiredTest:false, newVersionAlias:'v2', highestVersionStage:'Sandbox', highestVersionFound:'v2']
	def testPublishedwv2v3Results = [newVersionRequiredTest:false, newVersionAlias:'v3', highestVersionStage:'Sandbox', highestVersionFound:'v3']
	def results = [testSimpleResults, testPublishedResults, testPublishedwv2Results, testPublishedwv2v3Results]
	def correctTests = 0
	def totalTests = 12
	def currentVersion = [alias:'v1', stage:'Published']
	tests.eachWithIndex{ test, index ->
		def targetVersion = 'v1'
		def newVerStage = 'Definition'
		println "Debug - Case " + index
		// cover the v1 case. if we have a version in Published state, then we need to find the latest version.
		def newVersionRequired = false
		if(currentVersion.stage == 'Published')
		{
			def oldVerNo = currentVersion.alias.substring(1).toInteger()
			def newVerNo = oldVerNo
			println "Debug - before versions closure - oldVerNo: " + oldVerNo + " newVerNo: " + newVerNo + " newVerStage: " + newVerStage
			test.versions.each { version ->
				def verNo = version.alias.substring(1).toInteger()
				if (newVerNo <= verNo) {
					newVerNo = verNo
					// track stage as we need to determine if we need to demote
					newVerStage = version.stage
					if(newVerStage != 'Published')
					{
						newVersionRequired = false
					}
				}
			}
			println "Debug - after versions closure - oldVerNo: " + oldVerNo + " newVerNo: " + newVerNo + " newVerStage: " + newVerStage
			if(newVerNo == oldVerNo && (newVerStage == 'Published'))
			{
				newVersionRequired = true
				newVerNo++
			}
			targetVersion = 'v' + newVerNo.toString()
		}
		println "Test " + index
		def test1 = (results[index].newVersionRequiredTest == newVersionRequired)
		println results[index].newVersionRequiredTest.toString() + ":" + newVersionRequired.toString() + " Test Evaluation: " + test1 
		def test2 = (results[index].newVersionAlias == targetVersion)
		println results[index].newVersionAlias + ":" + targetVersion + " Test Evaluation: " + test2
		def test3 = (results[index].highestVersionStage == newVerStage)
		println results[index].highestVersionStage + ":" + newVerStage + " Test Evaluation: " + test3
		if(test1) correctTests++
		if(test2) correctTests++
		if(test3) correctTests++
	}
	println "Score: " + ((correctTests / totalTests) * 100)
