import { Injectable } from '@angular/core';

import { MapPageComponent } from '../../features/pages/map-page/map-page.component';
import { Coords } from '../../Dto/maps-dtos';

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
    console.log(coords);
    this.mapPageInstance?.clearRoute();
    this.mapPageInstance?.drawRoute(coords);
  }

}