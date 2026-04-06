pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    parameters {
        booleanParam(
            name: 'RUN_UI',
            defaultValue: true,
            description: 'Запустить ui_tests'
        )
        booleanParam(
            name: 'RUN_MOBILE',
            defaultValue: true,
            description: 'Запустить mobile_tests'
        )
    }

    stages {
        stage('Run selected tests') {
            steps {
                script {
                    def branches = [:]

                    if (params.RUN_UI) {
                        branches['UI tests'] = {
                            build job: 'ui_tests', wait: true, propagate: true
                        }
                    }

                    if (params.RUN_MOBILE) {
                        branches['Mobile tests'] = {
                            build job: 'mobile_tests', wait: true, propagate: true
                        }
                    }

                    if (branches.isEmpty()) {
                        error('Не выбрана ни одна job для запуска')
                    }

                    parallel branches
                }
            }
        }
    }
}