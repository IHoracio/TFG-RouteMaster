# Stage 1: Build (Node 24)
FROM node:24-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npx ng build --configuration=production

# Stage 2: Web Server (Unprivileged Nginx)
FROM nginxinc/nginx-unprivileged:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Unprivileged images use port 8080 by default
COPY --from=build /app/dist/routemaster/browser /usr/share/nginx/html

EXPOSE 8080
