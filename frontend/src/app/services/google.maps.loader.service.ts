import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { TranslationService } from './translation.service';

@Injectable({
  providedIn: 'root'
})
export class GoogleMapsLoaderService {
  private isLoaded = false;
  private loadPromise?: Promise<void>;

  private translation = inject(TranslationService);

  async ensureGoogleMapsLoaded(): Promise<void> {
    if (this.isLoaded) return Promise.resolve();
    if (this.loadPromise) return this.loadPromise;

    this.loadPromise = new Promise((resolve, reject) => {
      (window as any).initMap = () => {
        this.isLoaded = true;
        resolve();
      };

      const lang = this.translation.getCurrentLang().toLowerCase();

      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}&v=weekly&loading=async&libraries=places&language=${lang}&callback=initMap`;
      script.async = true;
      script.defer = true;
      script.onerror = (err) => {
        console.error('Error al cargar el script de Google Maps');
        reject(err);
      };
      
      document.head.appendChild(script);
    });

    return this.loadPromise;
  }

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