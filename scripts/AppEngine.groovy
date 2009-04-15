includeTargets << grailsScript("_GrailsWar")
target(cleanUpAfterWar:"override to avoid cleanup") { 
	// do nothing
}
target(main: "Runs a Grails application in the AppEngine development environment") {
	depends(parseArguments)
	
	ant.'import'(file:"${appEngineSDK}/config/user/ant-macros.xml")
	ant.delete(dir:"${grailsSettings.projectWorkDir}/staging")
	war()
	
	def cmd = argsMap.params ? argsMap.params[0] : 'run'
	
	switch(cmd) {
		case 'run':
			ant.dev_appserver(war:stagingDir); break
		case ~/(update|deploy)/:
			ant.appcfg(action:"update", war:stagingDir); break
		case ~/(update_indexes)/:
			ant.appcfg(action:"update_indexes", war:stagingDir); break			
		case 'rollback':
			ant.appcfg(action:"rollback", war:stagingDir); break		
		case 'logs':
			def days = argsMap.days ?: 5
			def file = argsMap.file ?: 'logs.txt'
			
			ant.appcfg(action:"rollback", war:stagingDir) {
				options {
					arg value:"--num_days=$days"
				}
				args {
					arg value:file
				}
			}
		
		break
		default: ant.dev_appserver(war:stagingDir);
	}
}

setDefaultTarget(main)