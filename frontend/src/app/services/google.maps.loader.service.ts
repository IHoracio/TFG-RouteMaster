import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GoogleMapsLoaderService {
  private isLoaded = false;
  private loadPromise?: Promise<void>;

  async ensureGoogleMapsLoaded(): Promise<void> {
    if (this.isLoaded) return Promise.resolve();
    if (this.loadPromise) return this.loadPromise;

    this.loadPromise = new Promise((resolve, reject) => {
      (window as any).initMap = () => {
        this.isLoaded = true;
        resolve();
      };

      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}&v=weekly&loading=async&libraries=places&callback=initMap`;
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