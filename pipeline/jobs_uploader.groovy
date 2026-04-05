import groovy.transform.Field

@Field def CONF_FILE = "./config.ini"
@Field def JOBS_DIR = "./jobs"

node {
    currentBuild.description = "<p style='color: green;'>Jobs uploader</p>"

    stage('Checkout') {
        checkout scm
    }

    stage('Create config.ini') {
        withCredentials([usernamePassword(credentialsId: 'jenkins', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_PASS')]) {
            def jenkinsUrl = (env.JENKINS_URL ?: 'http://localhost:8080/').trim()
            sh """
            cat > ${CONF_FILE} <<EOF
[jenkins]
url=${jenkinsUrl}
user=${JENKINS_USER}
password=${JENKINS_PASS}

[job_builder]
recursive=True
keep_descriptions=False
EOF
            """
        }
    }

    stage('Deploy jobs to Jenkins') {
        sh "jenkins-jobs --conf ${CONF_FILE} --flush-cache update ${JOBS_DIR}"
    }
}
