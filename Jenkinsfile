pipeline {
    agent any

    environment {
        // AWS
        AWS_REGION = 'us-east-1'
        S3_BUCKET = 'route-master-frontend'
        CLOUDFRONT_DIST_ID = 'E27QH8YNNPVJHF'

        // SpringBoot
        JAR_NAME = 'routemaster-0.0.1-SNAPSHOT.jar'
        SPRING_LOG = 'spring.log'

        // Credentials
        GOOGLE_KEY = credentials('google-api-key')
        OPENWEATHER_KEY = credentials('openweather-api-key')
        COOKIE_AUTH_SECRET_KEY = credentials('auth-secret-key')
        SSL_KEYSTORE_PASSWD = credentials('ssl-keystore-passwd')
        DATABASE_URL = credentials('database-url')
        DATABASE_USER = credentials('database-user')
        DATABASE_PASSWD = credentials('database-passwd')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/IHoracio/TFG-RouteMaster.git'
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean package'
                }
            }
        }

        stage('Deploy Backend (separate container)') {
            steps {
                withCredentials([file(credentialsId: 'ssl-keystore-p12', variable: 'KEYSTORE_FILE')]) {
                    sh '''
                        set -euo pipefail

                        HOST_DIR="/opt/routemaster"

                        # Copy jar to host-mounted directory
                        cp "backend/target/${JAR_NAME}" "${HOST_DIR}/app.jar"

                        # Copy keystore to host-mounted directory
                        mkdir -p "${HOST_DIR}/keystore"
                        cp "$KEYSTORE_FILE" "${HOST_DIR}/keystore/keystore.p12"
                        chmod 600 "${HOST_DIR}/keystore/keystore.p12" || true

                        # Backend container on the host via docker.sock
                        docker rm -f routemaster-backend || true

                        docker run -d --name routemaster-backend \
                          --restart unless-stopped \
                          --env-file "${HOST_DIR}/backend.env" \
                          -p 8443:8443 \
                          -v "${HOST_DIR}/app.jar:/opt/routemaster/app.jar:ro" \
                          -v "${HOST_DIR}/keystore:/opt/routemaster/keystore:ro" \
                          -v "${HOST_DIR}/logs:/opt/routemaster/logs" \
                          eclipse-temurin:21-jre \
                          java -jar /opt/routemaster/app.jar

                        docker ps | grep routemaster-backend
                        docker logs --tail=50 routemaster-backend || true
                    '''
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm ci'

                    sh '''
                        set -euo pipefail
                        envsubst < src/environments/environment.prod.ts > src/environments/environment.prod.ts.tmp
                        mv src/environments/environment.prod.ts.tmp src/environments/environment.prod.ts
                    '''

                    sh 'npx ng build --configuration=production'
                }
            }
        }

        stage('Deploy Frontend') {
            steps {
                sh 'aws s3 sync frontend/dist/angular/browser/ s3://${S3_BUCKET}/ --delete'
                sh 'aws cloudfront create-invalidation --distribution-id ${CLOUDFRONT_DIST_ID} --paths "/*"'
            }
        }
    }

    post {
        success { echo 'CI/CD exitoso!' }
        failure {
            echo 'CI/CD fallido.'
            sh 'docker logs --tail=200 routemaster-backend || true'
        }
    }
}
