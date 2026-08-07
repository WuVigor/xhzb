pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    tools {
        maven 'maven'
        jdk 'jdk17'
    }

    parameters {
        string(name: 'services', defaultValue: 'xhzb-admin', description: '需要构建的服务，多个服务使用逗号分隔')
        string(name: 'GIT_URL', defaultValue: 'https://gitee.com/tianlangqiqing/xhzb-bj02.git', description: 'Git 仓库地址')
        string(name: 'GIT_TAG', defaultValue: 'master', description: 'Git 分支或标签')
        string(name: 'DOCKER_TAG', defaultValue: 'latest', description: 'Docker 镜像标签')
    }

    stages {
        stage('清理工作空间') {
            steps {
                cleanWs()
            }
        }

        stage('检查构建环境') {
            steps {
                sh 'java -version'
                sh '/var/jenkins_home/maven/bin/mvn -version'
            }
        }

        stage('拉取 Git 代码') {
            steps {
                echo "当前分支：${params.GIT_TAG}，当前服务：${params.services}"
                checkout([$class: 'GitSCM',
                          branches: [[name: params.GIT_TAG]],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [],
                          submoduleCfg: [],
                          userRemoteConfigs: [[credentialsId: 'Gitee_ID', url: params.GIT_URL]]
                ])
            }
        }

        stage('Maven 打包') {
            steps {
                sh '/var/jenkins_home/maven/bin/mvn clean install -DskipTests'
            }
        }

        stage('构建 Docker 镜像') {
            steps {
                script {
                    for (serviceName in params.services.tokenize(',')) {
                        sh "docker build -t ${serviceName}:${params.DOCKER_TAG} -f ./${serviceName}/Dockerfile ./${serviceName}/target"
                    }
                }
            }
        }

        stage('部署服务') {
            steps {
                script {
                    for (serviceName in params.services.tokenize(',')) {
                        sh "chmod +x ./${serviceName}/deploy.sh"
                        sh "./${serviceName}/deploy.sh ${serviceName} ${params.DOCKER_TAG}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo '任务构建完毕'
        }
    }
}
