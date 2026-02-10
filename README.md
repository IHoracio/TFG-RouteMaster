# TFG-RouteMaster

## Descripción
Herramienta web interactiva para planificar rutas de viaje en coche. Permite a los usuarios calcular trayectos eficientes entre origen, destino y puntos intermedios, optimizando rutas con información adicional como gasolineras cercanas, pronósticos meteorológicas y preferencias personalizadas del vehículo (tipo de combustible, etiqueta medioambiental, marcas favoritas).

La motivación surge de la necesidad de soluciones digitales para movilidad sostenible y segura, integrando APIs externas como Google Maps y Precioil, con despliegue en la nube (AWS).

## Características Principales
- **Cálculo de Rutas**: Trayectos optimizados con puntos intermedios opcionales.
- **Información Adicional**: Gasolineras en el recorrido, condiciones meteorológicas y opciones personalizadas.
- **Interfaz Interactiva**: Buscador y Mapas visuales con marcadores.
- **APIs Integradas**: Google (Drirections, Geocode, Weahter), Precioil (precios de combustible).
- **Despliegue en Nube**: Backend en AWS Lambda, BD en RDS.
- **Seguridad**: Autenticación con JWT y protección de APIs.

## Tecnologías Utilizadas
- **Backend**: Spring Boot (Java), JPA, MySQL/RDS.
- **Frontend**: Angular (TypeScript), RxJS, Google Maps API.
- **Despliegue**: AWS (Lambda, RDS).
- **Herramientas**: Git, GitHub, Maven, Node.js.

## Instalación y Configuración
1. **Clona el repositorio**: `git clone https://github.com/IHoracio/TFG-RouteMaster.git`

2. **Backend (Spring Boot)**:
   - Configura MySQL local o AWS RDS.
   - Ejecuta: `mvn spring-boot:run`

3. **Frontend (Angular)**:
   - Instala dependencias: `npm install`
   - Ejecuta: `ng serve`
   - Abre en `http://localhost:4200`

4. **APIs**: Obtén claves de Google y configúrala en `backend/src/main/resources/application.properties` dentro del backend y `frontend/src/environments/environment.ts` en el frontend.
   
   Tienes de ejemplo el `backend/src/main/resources/application-example.properties` y `frontend/src/environments/environment-example.ts`

## Uso
- (Opcional) Regístrate y configura preferencias de vehículo.
- Ingresa origen/destino, calcula ruta.
- Visualiza gasolineras y clima en el mapa.
- Comparte la ruta creada.
- (Necesitas tener sesion iniciada) Guardala como favorita 

## Licencia
Este proyecto es para fines educativos.

---
**Proyecto desarrollado inicialmente en equipo (4 personas, incluyéndome) durante mi formacion en Metrica, una duracion de mes y medio, y continuado de forma individual para el TFG, cumpliendo los siguientes nuevos objetivos [Backlog Sprint](https://github.com/users/IHoracio/projects/2).**  
