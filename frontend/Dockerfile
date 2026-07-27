# Fase 1: Compilación (Node.js)
FROM node:24 AS build
WORKDIR /app
COPY package*.json ./
# npm ci es más seguro y rápido que npm install para entornos de construcción
RUN npm ci
COPY . .
RUN npx ng build --configuration=production

# Fase 2: Servidor Web (Nginx)
FROM nginx:alpine
# Copiamos nuestra configuración personalizada de enrutamiento
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copiamos los archivos estáticos de Angular (basado en la ruta de tu Jenkinsfile)
COPY --from=build /app/dist/angular/browser /usr/share/nginx/html
EXPOSE 80
