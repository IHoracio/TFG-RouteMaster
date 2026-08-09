// Declarative Pipeline for RouteMaster CI/CD
pipeline {
    agent any

    // -------------------------------------------------------------------------
    // ENVIRONMENT VARIABLES
    // -------------------------------------------------------------------------
    environment {
        GOOGLE_KEY = credentials('google-api-key')
        OPENWEATHER_KEY = credentials('openweather-api-key')
        COOKIE_AUTH_SECRET_KEY = credentials('auth-secret-key')
        DATABASE_URL = credentials('database-url')
        DB_USER = credentials('database-user')
        DB_PASSWORD = credentials('database-passwd')
        CLOUDFLARE_TOKEN = credentials('cloudflare-token')
    }

    stages {
        // -------------------------------------------------------------------------
        // STAGE 1: CHECKOUT
        // -------------------------------------------------------------------------
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/IHoracio/TFG-RouteMaster.git'
            }
        }

        // -------------------------------------------------------------------------
        // STAGE 2: DATABASE HEALTH CHECK
        // -------------------------------------------------------------------------
        stage('Check Database Connection') {
            steps {
                sh '''
                    echo "Checking if MySQL database is up and running..."
                    if ! docker exec routemaster-db mysqladmin ping -u"${DB_USER}" -p"${DB_PASSWORD}" --silent; then
                        echo "ERROR: Database is not responding or credentials are incorrect."
                        exit 1
                    fi
                    echo "SUCCESS: Database connection verified."
                '''
            }
        }

        // -------------------------------------------------------------------------
        // STAGE 3: BACKEND TESTS
        // -------------------------------------------------------------------------
        stage('Backend Tests (Spring Boot)') {
            steps {
                sh '''
                    echo "Running Spring Boot tests..."
                    docker run --rm \
                        -v "${WORKSPACE}/backend:/app" \
                        -v maven-cache:/root/.m2 \
                        -w /app \
                        maven:3.9.6-eclipse-temurin-21-alpine \
                        mvn test
                '''
            }
        }

        // -------------------------------------------------------------------------
        // STAGE 4: FRONTEND TESTS
        // -------------------------------------------------------------------------
        stage('Frontend Tests (Angular)') {
            steps {
                sh '''
                    echo "Running Angular tests..."

                    cat << 'EOF' > frontend/run-tests.sh
#!/bin/sh
set -e
apk add --no-cache chromium
echo '#!/bin/sh' > /usr/bin/chromium-wrapper
echo 'exec /usr/bin/chromium-browser --no-sandbox "$@"' >> /usr/bin/chromium-wrapper
chmod +x /usr/bin/chromium-wrapper
export CHROME_BIN=/usr/bin/chromium-wrapper
npm ci
npx ng test --watch=false --browsers=ChromeHeadless
EOF

                    chmod +x frontend/run-tests.sh
                    docker run --rm \
                        -v "${WORKSPACE}/frontend:/app" \
                        -w /app \
                        node:24-alpine \
                        /app/run-tests.sh
                    rm frontend/run-tests.sh
                '''
            }
        }

        // -------------------------------------------------------------------------
        // STAGE 5: DEPLOYMENT (CD)
        // -------------------------------------------------------------------------
        stage('Deploy via Docker Compose') {
            steps {
                sh '''
                    set -euo pipefail

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

                    # 1. Eliminamos SOLAMENTE backend y frontend
                    docker rm -f routemaster-backend routemaster-frontend || true

                    # 2. Desplegamos usando el nombre limpio del proyecto
                    docker compose -p routemaster up -d --build --no-deps backend frontend

                    rm -f .env

                    echo "Waiting for MySQL database to be truly ready..."
                    counter=0
                    until docker exec routemaster-db mysqladmin ping -u"${DB_USER}" -p"${DB_PASSWORD}" --silent; do
                        counter=$((counter+1))
                        if [ $counter -gt 30 ]; then
                            echo "ERROR: Database did not wake up in time."
                            exit 1
                        fi
                        sleep 2
                    done

                    echo "Database is ready! Restarting backend to ensure a clean connection..."
                    docker restart routemaster-backend

                    echo "Waiting for backend to initialize..."
                    sleep 10

                    if [ "$(docker inspect -f '{{.State.Running}}' routemaster-backend)" != "true" ]; then
                        echo "ERROR: Backend container stopped unexpectedly."
                        docker logs routemaster-backend
                        exit 1
                    fi

                    if docker logs routemaster-backend 2>&1 | grep -E -i "Communications link failure|SQLException|Access denied|Connection refused"; then
                        echo "ERROR: Database connection failed during runtime!"
                        docker logs --tail=100 routemaster-backend
                        exit 1
                    else
                        echo "SUCCESS: Backend is running and database connection is healthy!"
                    fi
                '''
            }
        }
    }

    // -------------------------------------------------------------------------
    // POST ACTIONS
    // -------------------------------------------------------------------------
   post {
        success {
            echo 'Pipeline completed successfully! Tests passed and deployment is live.'
            echo 'Restarting Cloudflare tunnel...'
            sh 'docker compose -p routemaster restart cloudflared || true'
        }
        failure {
            echo 'Pipeline failed. Printing backend logs...'
            sh 'docker logs --tail=200 routemaster-backend || true'
        }
    }
}
