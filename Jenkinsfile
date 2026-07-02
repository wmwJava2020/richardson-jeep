pipeline {
    agent any

    tools {
        maven 'maven-3.9'
        jdk 'jdk-17'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        IMAGE_NAMESPACE = "${DOCKERHUB_CREDENTIALS_USR}"
        SERVICES = "discovery-server config-server api-gateway inventory-service order-service notification-service customer-service"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn -B clean verify'
            }
        }

        stage('Docker Build & Push') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    echo "$DOCKERHUB_CREDENTIALS_PSW" | docker login -u "$DOCKERHUB_CREDENTIALS_USR" --password-stdin

                    for svc in $SERVICES; do
                        docker build -t "$IMAGE_NAMESPACE/richardson-jeep-$svc:$GIT_COMMIT" \
                                      -t "$IMAGE_NAMESPACE/richardson-jeep-$svc:latest" \
                                      -f "$svc/Dockerfile" .
                        docker push "$IMAGE_NAMESPACE/richardson-jeep-$svc:$GIT_COMMIT"
                        docker push "$IMAGE_NAMESPACE/richardson-jeep-$svc:latest"
                    done
                '''
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    echo "Deploy step placeholder."
                    echo "Typical options:"
                    echo "  - kubectl apply -f k8s/ (against a configured cluster context)"
                    echo "  - docker compose -f docker-compose.yml up -d over SSH to the target host"
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
