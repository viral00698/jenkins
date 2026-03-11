pipeline {
    agent { label 'SpringAgent' }

    environment {
        IMAGE_NAME = "myapp-springboot-app"
        CONTAINER_NAME = "springboot-app"
        POSTGRES_CONTAINER = "postgres-db"
        POSTGRES_DB = "Test"
        POSTGRES_USER = "postgres"
        POSTGRES_PASSWORD = "root"
        POSTGRES_PORT = "5432"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/viral00698/jenkins.git'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn clean test -Dspring.profiles.active=dev'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build WAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build(env.IMAGE_NAME)
                }
            }
        }

        stage('Deploy Container') {
            steps {
                script {
                    // Stop old container if it exists
                    sh "docker stop ${env.CONTAINER_NAME} || true"
                    sh "docker rm ${env.CONTAINER_NAME} || true"

                    // Run new container with dev profile and Postgres envs
                    sh """
                        docker run -d \
                        --name ${env.CONTAINER_NAME} \
                        --network jenkins-net \
                        -p 8085:8080 \
                        -e SPRING_PROFILES_ACTIVE=dev \
                        -e SPRING_DATASOURCE_URL=jdbc:postgresql://${env.POSTGRES_CONTAINER}:${env.POSTGRES_PORT}/${env.POSTGRES_DB} \
                        -e SPRING_DATASOURCE_USERNAME=${env.POSTGRES_USER} \
                        -e SPRING_DATASOURCE_PASSWORD=${env.POSTGRES_PASSWORD} \
                        ${env.IMAGE_NAME}
                    """
                }
            }
        }
    }
}
