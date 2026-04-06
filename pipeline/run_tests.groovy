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
                    def builds = [:]
                    def results = [:]

                    if (params.RUN_UI) {
                        branches['UI tests'] = {
                            def buildResult = build job: 'ui_tests', wait: true, propagate: false
                            builds['ui'] = buildResult.number
                            results['ui_tests'] = buildResult.result
                            echo "ui_tests build #${buildResult.number} result: ${buildResult.result}"
                        }
                    }

                    if (params.RUN_MOBILE) {
                        branches['Mobile tests'] = {
                            def buildResult = build job: 'mobile_tests', wait: true, propagate: false
                            builds['mobile'] = buildResult.number
                            results['mobile_tests'] = buildResult.result
                            echo "mobile_tests build #${buildResult.number} result: ${buildResult.result}"
                        }
                    }

                    if (branches.isEmpty()) {
                        error('Не выбрана ни одна job для запуска')
                    }

                    parallel branches

                    env.UI_BUILD = builds['ui']?.toString()
                    env.MOBILE_BUILD = builds['mobile']?.toString()

                    echo "Final child job results: ${results}"

                    if (results.values().any { it == 'FAILURE' }) {
                        currentBuild.result = 'UNSTABLE'
                    } else if (results.values().any { it == 'UNSTABLE' }) {
                        currentBuild.result = 'UNSTABLE'
                    } else {
                        currentBuild.result = 'SUCCESS'
                    }
                }
            }
        }

        stage('Collect Allure results') {
            steps {
                script {
                    if (params.RUN_UI && env.UI_BUILD) {
                        copyArtifacts(
                            projectName: 'ui_tests',
                            selector: specific(env.UI_BUILD),
                            filter: 'build/allure-results/**',
                            target: 'allure/ui'
                        )
                    }

                    if (params.RUN_MOBILE && env.MOBILE_BUILD) {
                        copyArtifacts(
                            projectName: 'mobile_tests',
                            selector: specific(env.MOBILE_BUILD),
                            filter: 'build/allure-results/**',
                            target: 'allure/mobile'
                        )
                    }
                }
            }
        }

        stage('Merge Allure results') {
            steps {
                sh '''
                  set -eux
                  mkdir -p merged-allure-results
                  if [ -d allure ]; then
                    find allure -type f | while read file; do
                      cp "$file" merged-allure-results/
                    done
                  fi
                  ls -la merged-allure-results || true
                '''
            }
        }

        stage('Publish Allure report') {
            steps {
                allure([
                    includeProperties: false,
                    results: [[path: 'merged-allure-results']]
                ])
            }
        }
    }
}