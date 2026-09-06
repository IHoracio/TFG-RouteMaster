pipeline {
    agent any

    // -------------------------------------------------------------------------
    // ENVIRONMENT VARIABLES
    // -------------------------------------------------------------------------
    environment {
        // Backend keys
        GOOGLE_KEY = credentials('google-api-key')
        // Frontend key (Make sure to create this credential in Jenkins!)
        GOOGLE_KEY_FRONTEND = credentials('google-api-key-frontend') 
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
        // STAGE 2: PREPARE FRONTEND ENVIRONMENT
        // -------------------------------------------------------------------------
        stage('Inject Frontend Secrets') {
            steps {
                sh '''
                    echo "Injecting Google Maps API Key into Angular environment.prod.ts..."
                    # We use sed to replace the placeholder with the actual Jenkins credential
                    # The backslash (\\) escapes the $ so sed looks for the literal string '${GOOGLE_KEY_FRONTEND}'
                    sed -i "s|\\\${GOOGLE_KEY_FRONTEND}|${GOOGLE_KEY_FRONTEND}|g" frontend/src/environments/environment.prod.ts
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

                    # Clean credentials from carriage returns or spaces
                    CLEAN_USER=$(printf '%s' "${DB_USER}" | tr -d '\r\n ')
                    CLEAN_PASS=$(printf '%s' "${DB_PASSWORD}" | tr -d '\r\n ')
                    CLEAN_ROOT=$(printf '%s' "${DB_PASSWORD}" | tr -d '\r\n ')

                    echo "Writing .env with cleaned credentials for the backend..."
                    cat <<EOF > .env
DATABASE_URL=${DATABASE_URL}
DB_USER=${CLEAN_USER}
DB_PASSWORD=${CLEAN_PASS}
DB_ROOT_PASSWORD=${CLEAN_ROOT}
CLOUDFLARE_TOKEN=${CLOUDFLARE_TOKEN}
SPRING_PROFILES_ACTIVE=prod
GOOGLE_KEY=${GOOGLE_KEY}
OPENWEATHER_KEY=${OPENWEATHER_KEY}
COOKIE_AUTH_SECRET_KEY=${COOKIE_AUTH_SECRET_KEY}
EOF

                    # Remove any running service containers from previous runs (ignore errors)
                    docker rm -f routemaster-db routemaster-backend routemaster-frontend || true

                    # Build and start services
                    # If you face caching issues (like the perl apk add getting stuck), you can add --no-cache to the build command:
                    # docker compose build --no-cache routemaster-frontend
                    docker compose up -d --build routemaster-db routemaster-backend routemaster-frontend

                    echo "Waiting for MySQL database to be truly ready (using app user)..."
                    counter=0
                    until docker exec routemaster-db mysqladmin ping -u"${CLEAN_USER}" -p"${CLEAN_PASS}" --silent; do
                        counter=$((counter+1))
                        if [ $counter -gt 60 ]; then
                            echo "ERROR: Database did not wake up in time."
                            docker logs --tail=200 routemaster-db || true
                            exit 1
                        fi
                        sleep 2
                    done

                    echo "Ping successful! Waiting 10 seconds for MySQL to open TCP connections..."
                    sleep 10

                    echo "Database is fully ready! Restarting backend to ensure a clean connection..."
                    docker restart routemaster-backend

                    echo "Waiting for backend to initialize..."
                    sleep 15

                    if [ "$(docker inspect -f '{{.State.Running}}' routemaster-backend)" != "true" ]; then
                        echo "ERROR: Backend container stopped unexpectedly."
                        docker logs --tail=200 routemaster-backend || true
                        exit 1
                    fi

                    if docker logs routemaster-backend 2>&1 | grep -E -i "Communications link failure|SQLException|Access denied|Connection refused"; then
                        echo "ERROR: Database connection failed during runtime!"
                        docker logs --tail=200 routemaster-backend || true
                        exit 1
                    else
                        echo "SUCCESS: Backend is running and database connection is healthy!"
                    fi
                '''
            }
        }

        // -------------------------------------------------------------------------
        // STAGE 6: DATABASE HEALTH CHECK
        // -------------------------------------------------------------------------
        stage('Check Database Connection') {
            steps {
                sh '''
                    echo "Checking if MySQL database is up and running..."
                    CLEAN_USER=$(printf '%s' "${DB_USER}" | tr -d '\r\n ')
                    CLEAN_PASS=$(printf '%s' "${DB_PASSWORD}" | tr -d '\r\n ')
                    if ! docker exec routemaster-db mysqladmin ping -u"${CLEAN_USER}" -p"${CLEAN_PASS}" --silent; then
                        echo "ERROR: Database is not responding or credentials are incorrect."
                        docker logs --tail=200 routemaster-db || true
                        exit 1
                    fi
                    echo "SUCCESS: Database connection verified (again!)."
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
            sh 'docker compose restart cloudflared || true'
        }
        failure {
            echo 'Pipeline failed. Printing backend logs...'
            sh 'docker logs --tail=200 routemaster-backend || true'
        }
    }
}
