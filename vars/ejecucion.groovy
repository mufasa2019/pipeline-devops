/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/

def call(){
    pipeline {
        agent any
        environment {
            NEXUS_USER         = credentials('NEXUS-USER')
            NEXUS_PASSWORD     = credentials('NEXUS-PASS')
            }
        parameters {
            choice(
                name:'compileTool',
                choices: ['Maven', 'Gradle'],
                description: 'Seleccione herramienta de compilacion'
            )
            text description: '''enviar los stages separados por ";" Vacio si necesita todos los stages''', name: 'stages'
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                    switch(params.stages)
                        {
                            case 'Maven':
                                maven.call(params.stages);
                            break;
                            case 'Gradle':
                                gradle.call(params.stages);
                            break;
                        }
                    }
                }
                post{
                    success{
                        slackSend color: 'good', message: "[Cristian] [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'Slack01'
                    }
                    failure{
                        slackSend color: 'danger', message: "[Cristian] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'Slack01'
                    }
                }
            }
        }
    }
}

return this;