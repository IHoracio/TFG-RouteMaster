# Stage 1: Build the Angular app using Node 24
FROM node:24-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npx ng build --configuration=production

# Stage 2: Serve static files with unprivileged Nginx
FROM nginxinc/nginx-unprivileged:alpine

# Install perl requires root privileges during build, switch to root temporarily
USER root
RUN apk add --no-cache perl

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built static files from the build stage
COPY --from=build /app/dist/angular/browser /usr/share/nginx/html

# Copy the runtime entrypoint that will inject GOOGLE_KEY into static files
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Ensure the nginx user can read/write the files we may modify at runtime
RUN chown -R nginx:nginx /usr/share/nginx/html || true

# Switch back to the unprivileged nginx user for security
USER nginx

# The unprivileged image uses port 8080 by default
EXPOSE 8080

# Run the entrypoint (which performs the replacement) and then start nginx
ENTRYPOINT ["/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
