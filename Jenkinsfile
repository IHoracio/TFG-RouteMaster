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
            steps { git branch: 'main', url: 'https://github.com/IHoracio/TFG-RouteMaster.git' }
        }
        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean package'
                }
            }
        }
	stage('Deploy Backend') {
	  steps {
	    withCredentials([file(credentialsId: 'ssl-keystore-p12', variable: 'KEYSTORE_FILE')]) {
	      sh '''
	        set -eu
	        mkdir -p /var/jenkins_home/keystore
	        cp "$KEYSTORE_FILE" /var/jenkins_home/keystore/keystore.p12
	        pkill -f "${JAR_NAME}" || true
	        nohup java -jar backend/target/${JAR_NAME} > ${SPRING_LOG} 2>&1 &
	      '''
	    }
	  }
	}
	stage('Build Frontend') {
	  steps {
	    dir('frontend') {
	      sh 'npm ci'
	
	      sh '''
	        set -eu
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
        failure { echo 'CI/CD fallido.' }
    }
}
