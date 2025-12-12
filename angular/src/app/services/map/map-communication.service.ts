import { Injectable } from '@angular/core';
import { Coords } from '../../features/pages/map-page/Utils/google-route.mapper';
import { MapPageComponent } from '../../features/pages/map-page/map-page.component';

@Injectable({
  providedIn: 'root'
})
export class MapCommunicationService {
  private mapPageInstance: MapPageComponent | null = null;

  registerMapPage(instance: MapPageComponent): boolean {
    if (this.mapPageInstance && this.mapPageInstance !== instance) {
      console.warn('Ya existe una instancia de MapPageComponent');
      return false;
    }
    this.mapPageInstance = instance;
    return true;
  }

  constructor() {}

  sendRoute(coords: Coords[]): void {
    console.log("Estoy en el serviece");
    console.log(coords)
    this.mapPageInstance?.drawRoute(coords);
  }

}