// Declarative Pipeline for RouteMaster CI/CD
pipeline {
    agent any
    
    // -------------------------------------------------------------------------
    // ENVIRONMENT VARIABLES
    // Securely inject API keys, database credentials, and Cloudflare tokens
    // from Jenkins Credentials manager to prevent hardcoding sensitive data.
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
        // Fetches the latest source code from the 'main' branch in GitHub.
        // -------------------------------------------------------------------------
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/IHoracio/TFG-RouteMaster.git'
            }
        }
        
        // -------------------------------------------------------------------------
        // STAGE 2: DATABASE HEALTH CHECK
        // Fails fast if the database container is down or credentials are wrong.
        // Prevents the pipeline from building code if the deployment will inevitably fail.
        // -------------------------------------------------------------------------
        stage('Check Database Connection') {
            steps {
                sh '''
                    echo "Checking if MySQL database is up and running..."
                    
                    # Use mysqladmin inside the existing DB container to ping the database
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
        // Runs Spring Boot tests in an ephemeral Maven container.
        // Uses a Docker volume (maven-cache) to cache dependencies and speed up builds.
        // -------------------------------------------------------------------------
        stage('Backend Tests (Spring Boot)') {
            steps {
                sh '''
                    echo "Running Spring Boot tests..."
                    
                    # Spin up a temporary Maven container, mount the backend code, and run tests
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
        // Runs Angular tests in an ephemeral Node.js container.
        // Injects a wrapper script to run Chrome Headless in --no-sandbox mode,
        // which is required since Docker runs as the root user.
        // -------------------------------------------------------------------------
        stage('Frontend Tests (Angular)') {
            steps {
                sh '''
                    echo "Running Angular tests..."
                    
                    # 1. Create a temporary script in the frontend folder with the exact execution steps
                    cat << 'EOF' > frontend/run-tests.sh
#!/bin/sh
set -e

# Install Chromium browser
apk add --no-cache chromium

# Create a wrapper for Chrome to automatically include the --no-sandbox flag
echo '#!/bin/sh' > /usr/bin/chromium-wrapper
echo 'exec /usr/bin/chromium-browser --no-sandbox "$@"' >> /usr/bin/chromium-wrapper
chmod +x /usr/bin/chromium-wrapper

# Instruct Angular to use our custom Chrome wrapper
export CHROME_BIN=/usr/bin/chromium-wrapper

# Install dependencies and run the tests
npm ci
npx ng test --watch=false --browsers=ChromeHeadless
EOF
                    
                    # 2. Grant execution permissions to the temporary script
                    chmod +x frontend/run-tests.sh

                    # 3. Spin up a Node.js container to execute the script
                    docker run --rm \
                        -v "${WORKSPACE}/frontend:/app" \
                        -w /app \
                        node:24-alpine \
                        /app/run-tests.sh
                        
                    # 4. Clean up the temporary script to leave no trace
                    rm frontend/run-tests.sh
                '''
            }
        }

        // -------------------------------------------------------------------------
        // STAGE 5: DEPLOYMENT (CD)
        // Builds the new Docker images and deploys them alongside the Cloudflare Tunnel.
        // It injects environment variables temporarily and performs runtime health checks.
        // -------------------------------------------------------------------------
        stage('Deploy via Docker Compose') {
            steps {
                sh '''
                    set -euo pipefail

                    # Generate the .env file dynamically for Docker Compose
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
                    
                    # 1. Explicitly remove old backend, frontend, and tunnel containers to avoid naming conflicts
                    docker rm -f routemaster-backend routemaster-frontend cloudflared-tunnel || true

                    # 2. Deploy backend, frontend, and the new Cloudflare tunnel on the same Docker network
                    docker compose -p tfg-routemaster up -d --build --no-deps backend frontend cloudflared

                    # Remove the .env file immediately for security reasons
                    rm -f .env

                    echo "Waiting for backend to start..."
                    sleep 15

                    # Check if the backend container crashed during startup
                    if [ "$(docker inspect -f '{{.State.Running}}' routemaster-backend)" != "true" ]; then
                        echo "ERROR: Backend container stopped unexpectedly."
                        docker logs routemaster-backend
                        exit 1
                    fi

                    # Check the backend logs for common database connection errors
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
    // Executes logic based on the final status of the pipeline (Success/Failure).
    // -------------------------------------------------------------------------
    post {
        success {
            echo 'Pipeline completed successfully! Tests passed and deployment is live.'
        }
        failure {
            echo 'Pipeline failed. Printing the last 200 lines of backend logs for debugging...'
            sh 'docker logs --tail=200 routemaster-backend || true'
        }
    }
}
