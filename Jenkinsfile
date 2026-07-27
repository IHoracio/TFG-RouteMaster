pipeline {
    agent any

    environment {
        // Secure credentials extracted from Jenkins vault
        GOOGLE_KEY = credentials('google-api-key')
        OPENWEATHER_KEY = credentials('openweather-api-key')
        COOKIE_AUTH_SECRET_KEY = credentials('auth-secret-key')
        DATABASE_URL = credentials('database-url')
        DB_USER = credentials('database-user')
        DB_PASSWORD = credentials('database-passwd')
        CLOUDFLARE_TOKEN = credentials('cloudflare-token')
    }

    stages {
        stage('Checkout') {
            steps {
                // Pull latest source code from repository
                git branch: 'main', url: 'https://github.com/IHoracio/TFG-RouteMaster.git'
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    // Compile Spring Boot JAR with Maven wrapper
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Deploy via Docker Compose') {
            steps {
                sh '''
                    set -euo pipefail

                    # 1. Create a dynamic .env file using Jenkins secure credentials
                    echo "Generating dynamic .env file with secure credentials..."
                    cat <<EOF > .env
DATABASE_URL=${DATABASE_URL}
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD}
DB_ROOT_PASSWORD=${DB_PASSWORD}
CLOUDFLARE_TOKEN=${CLOUDFLARE_TOKEN}
SPRING_PROFILES_ACTIVE=prod
GOOGLE_KEY=${GOOGLE_KEY}
OPENWEATHER_KEY=${OPENWEATHER_KEY}
COOKIE_AUTH_SECRET_KEY=${COOKIE_AUTH_SECRET_KEY}
EOF

                    # 2. Deploy and build EVERYTHING inside containers via Docker Compose
                    # This forces Docker to use Node 24 inside the frontend Dockerfile automatically!
                    docker compose up -d --build

                    # 3. Security cleanup: Delete the .env file so secrets are not left on disk
                    rm .env

                    # Wait for Spring Boot to boot up and connect to DB
                    echo "Waiting for backend to start and connect to database..."
                    sleep 15

                    # Check if the container is running
                    if [ "$(docker inspect -f '{{.State.Running}}' routemaster-backend)" != "true" ]; then
                        echo "ERROR: Backend container stopped unexpectedly."
                        docker logs routemaster-backend
                        exit 1
                    fi

                    # Verify database connection in logs
                    if docker logs routemaster-backend 2>&1 | grep -E -i "Communications link failure|SQLException|Access denied|Connection refused"; then
                        echo "ERROR: Database connection failed!"
                        docker logs --tail=100 routemaster-backend
                        exit 1
                    else
                        echo "SUCCESS: Backend is running and database connection is healthy!"
                    fi
                '''
            }
        }
    }

    post {
        success { echo 'Pipeline completed successfully with Docker Compose!' }
        failure {
            echo 'Pipeline failed. Checking backend logs...'
            sh 'docker logs --tail=200 routemaster-backend || true'
        }
    }
}
