// Declarative Pipeline for RouteMaster CI/CD (mejorado y limpio)
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
        // DOCKER_GID puede no estar definido en algunos agentes; dejaremos que el shell haga el fallback
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
        // STAGE 2: BACKEND TESTS
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
        // STAGE 3: FRONTEND TESTS
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
        // STAGE 4: DEPLOYMENT (CD)
        // -------------------------------------------------------------------------
        stage('Deploy via Docker Compose') {
            steps {
                sh '''
                    set -euo pipefail

                    # Evitar warning si DOCKER_GID no está definido
                    export DOCKER_GID=${DOCKER_GID:-}

                    # Limpia credenciales de caracteres problemáticos
                    CLEAN_USER=$(printf '%s' "${DB_USER}" | tr -d '\r\n ')
                    CLEAN_PASS=$(printf '%s' "${DB_PASSWORD}" | tr -d '\r\n ')
                    CLEAN_ROOT=$(printf '%s' "${DB_PASSWORD}" | tr -d '\r\n ')

                    echo "Writing .env with cleaned credentials..."
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

                    # Protege el fichero .env
                    chmod 600 .env || true

                    # Remove any running service containers from previous runs (ignore errors)
                    docker rm -f routemaster-db routemaster-backend routemaster-frontend || true

                    # Levanta/reconstruye los servicios necesarios
                    docker compose up -d --build routemaster-db routemaster-backend routemaster-frontend

                    echo "Waiting for MySQL database to be truly ready (using app user)..."
                    counter=0
                    while true; do
                        # crear un archivo temporal seguro con credenciales y copiarlo al contenedor
                        TMP_CNF="$(mktemp)"
                        cat > "${TMP_CNF}" <<CNF
[client]
user=${CLEAN_USER}
password=${CLEAN_PASS}
CNF
                        chmod 600 "${TMP_CNF}"

                        docker cp "${TMP_CNF}" routemaster-db:/tmp/mysql.cnf || true
                        rm -f "${TMP_CNF}" || true

                        if docker exec routemaster-db mysqladmin --defaults-file=/tmp/mysql.cnf ping --silent 2>/dev/null; then
                            # limpiar dentro del contenedor
                            docker exec routemaster-db rm -f /tmp/mysql.cnf || true
                            break
                        fi

                        # limpiar y reintentar
                        docker exec routemaster-db rm -f /tmp/mysql.cnf || true
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

                    # Limpia .env para no dejar secretos en disco del agente
                    rm -f .env || true
                '''
            }
        }

        // -------------------------------------------------------------------------
        // STAGE 5: DATABASE HEALTH CHECK
        // -------------------------------------------------------------------------
        stage('Check Database Connection') {
            steps {
                sh '''
                    echo "Checking if MySQL database is up and running..."
                    CLEAN_USER=$(printf '%s' "${DB_USER}" | tr -d '\r\n ')
                    CLEAN_PASS=$(printf '%s' "${DB_PASSWORD}" | tr -d '\r\n ')

                    # Usar el mismo mecanismo seguro con defaults-file
                    TMP_CNF="$(mktemp)"
                    cat > "${TMP_CNF}" <<CNF
[client]
user=${CLEAN_USER}
password=${CLEAN_PASS}
CNF
                    chmod 600 "${TMP_CNF}"
                    docker cp "${TMP_CNF}" routemaster-db:/tmp/mysql.cnf || true
                    rm -f "${TMP_CNF}" || true

                    if ! docker exec routemaster-db mysqladmin --defaults-file=/tmp/mysql.cnf ping --silent; then
                        echo "ERROR: Database is not responding or credentials are incorrect."
                        docker logs --tail=200 routemaster-db || true
                        docker exec routemaster-db rm -f /tmp/mysql.cnf || true
                        exit 1
                    fi

                    # limpiar
                    docker exec routemaster-db rm -f /tmp/mysql.cnf || true
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
