import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { TranslationService } from './translation.service';

@Injectable({
  providedIn: 'root'
})
export class GoogleMapsLoaderService {
  private isLoaded = false;
  private loadPromise?: Promise<void>;
  private translation = inject(TranslationService);

  /**
   * Carga el script de Google Maps API. 
   * IMPORTANTE: Google Maps registra "Custom Elements" (Web Components) que empiezan por 'gmp-'.
   * Una vez registrados en el navegador, NO se pueden volver a definir ni cambiar su idioma
   * mediante la recarga del script en la misma sesión de la página (SPA).
   */
  async ensureGoogleMapsLoaded(): Promise<void> {
    // Si ya hay una carga en curso o finalizada, retornamos la misma promesa.
    if (this.loadPromise) return this.loadPromise;

    const lang = this.translation.getCurrentLang().toLowerCase();

    this.loadPromise = new Promise((resolve, reject) => {
      // Doble check de seguridad por si el objeto global ya existe
      if (window.google?.maps && this.isLoaded) {
        resolve();
        return;
      }

      (window as any).initMap = () => {
        this.isLoaded = true;
        resolve();
      };

      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}&v=weekly&loading=async&libraries=places&language=${lang}&callback=initMap`;
      script.async = true;
      script.defer = true;
      script.onerror = (err) => {
        this.loadPromise = undefined; // Si falla, permitimos reintentar en la próxima llamada
        reject(err);
      };
      
      document.head.appendChild(script);
    });

    return this.loadPromise;
  }

  // Métodos para importar librerías individuales manteniendo el estado de carga
  async getPlacesLibrary(): Promise<google.maps.PlacesLibrary> {
    await this.ensureGoogleMapsLoaded();
    return await google.maps.importLibrary('places') as google.maps.PlacesLibrary;
  }

  async getMapsLibrary(): Promise<google.maps.MapsLibrary> {
    await this.ensureGoogleMapsLoaded();
    return await google.maps.importLibrary('maps') as google.maps.MapsLibrary;
  }

  async getMarkerLibrary(): Promise<google.maps.MarkerLibrary> {
    await this.ensureGoogleMapsLoaded();
    return await google.maps.importLibrary('marker') as google.maps.MarkerLibrary;
  }
}