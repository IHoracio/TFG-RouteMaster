pipeline {
    agent any
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
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/IHoracio/TFG-RouteMaster.git'
            }
        }
        
        stage('Check Database Connection') {
            steps {
                sh '''
                    echo "Comprobando que la base de datos MySQL está operativa..."
                    if ! docker exec routemaster-db mysqladmin ping -u"${DB_USER}" -p"${DB_PASSWORD}" --silent; then
                        echo "ERROR: La base de datos no está respondiendo o las credenciales son incorrectas."
                        exit 1
                    fi
                    echo "SUCCESS: Conexión a la base de datos verificada."
                '''
            }
        }

        stage('Backend Tests (Spring Boot)') {
            steps {
                sh '''
                    echo "Ejecutando tests de Spring Boot..."
                    docker run --rm \
                        -v "${WORKSPACE}/backend:/app" \
                        -v maven-cache:/root/.m2 \
                        -w /app \
                        maven:3.9.6-eclipse-temurin-21-alpine \
                        mvn test
                '''
            }
        }

        stage('Frontend Tests (Angular)') {
            steps {
                sh '''
                    echo "Ejecutando tests de Angular..."
                    docker run --rm \
                        -v "${WORKSPACE}/frontend:/app" \
                        -w /app \
                        node:24-alpine \
                        sh -c "apk add --no-cache chromium && export CHROME_BIN=/usr/bin/chromium-browser && npm ci && npx ng test --watch=false --browsers=ChromeHeadless"
                '''
            }
        }

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
                    
                    # 1. Eliminamos backend, frontend y el TUNEL VIEJO para evitar conflictos
                    docker rm -f routemaster-backend routemaster-frontend cloudflared-tunnel || true

                    # 2. Desplegamos backend, frontend y el NUEVO TUNEL en la misma red
                    docker compose -p tfg-routemaster up -d --build --no-deps backend frontend cloudflared

                    rm -f .env

                    echo "Waiting for backend to start..."
                    sleep 15

                    if [ "$(docker inspect -f '{{.State.Running}}' routemaster-backend)" != "true" ]; then
                        echo "ERROR: Backend container stopped unexpectedly."
                        docker logs routemaster-backend
                        exit 1
                    fi

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
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Checking backend logs...'
            sh 'docker logs --tail=200 routemaster-backend || true'
        }
    }
}
