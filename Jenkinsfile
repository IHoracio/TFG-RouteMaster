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
                    
                    # 1. Eliminamos los contenedores explícitamente para evitar conflictos de nombres
                    docker rm -f routemaster-backend routemaster-frontend || true

                    # 2. Usamos el flag "-p tfg-routemaster" para mantener la misma red y stack original
                    docker compose -p tfg-routemaster up -d --build --no-deps backend frontend

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
