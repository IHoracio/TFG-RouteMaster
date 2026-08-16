# Stage 1: Build (Node 24)
FROM node:24-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npx ng build --configuration=production

# Stage 2: Web Server (Unprivileged Nginx)
FROM nginxinc/nginx-unprivileged:alpine

# instalar perl para permitir reemplazos seguros en runtime
RUN apk add --no-cache perl

COPY nginx.conf /etc/nginx/conf.d/default.conf

# Unprivileged images use port 8080 by default
COPY --from=build /app/dist/angular/browser /usr/share/nginx/html

# Copiar entrypoint que inyecta GOOGLE_KEY en ficheros estáticos
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
