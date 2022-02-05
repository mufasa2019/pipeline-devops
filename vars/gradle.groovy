import pipeline.*

def call(String chosenStages){

	//figlet 'gradle'

	def pipelineStages = ['buildAndTest','sonar','runJar','rest','nexus']

	def utils  = new test.UtilMethods()
	def stages = utils.getValidatedStages(chosenStages, pipelineStages)

	stages.each{
		stage(it){
			try {
				"${it}"()
			}
			catch(Exception e) {
				error "Stage ${it} tiene problemas: ${e}"
			}
		}
	}
}

def buildAndTest(){
	sh './gradlew clean build'
}

def sonar(){
	def sonarhome = tool 'sonarqube'
    //sh "${sonarhome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
    withSonarQubeEnv('sonarqube') {
        sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build'
    }
}

def runJar(){
	sh "nohup bash gradlew bootRun &"
	sleep 20S
}

def rest(){
	sh "curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing"
}

def nexus(){
nexusPublisher nexusInstanceId: 'nexus',
    nexusRepositoryId: 'ejemplo-gradle',
    packages: [
        [$class: 'MavenPackage',
            mavenAssetList: [
                [classifier: '',
                extension: 'jar',
                filePath: 'build/DevOpsUsach2020-0.0.1.jar'
            ]
        ],
            mavenCoordinate: [
                artifactId: 'DevOpsUsach2020',
                groupId: 'com.devopsusach2020',
                packaging: 'jar',
                version: '0.0.1'
            ]
        ]
    ]
}

return this;