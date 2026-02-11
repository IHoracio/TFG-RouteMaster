import { Injectable } from '@angular/core';

import { MapPageComponent } from '../../features/pages/map-page/map-page.component';
import { Coords } from '../../Dto/maps-dtos';
import { WeatherData } from '../../Dto/weather-dtos';
import { GasStation } from '../../Dto/gas-station';

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

  unregisterMapPage(instance: MapPageComponent): void {
    if (this.mapPageInstance === instance) {
      this.mapPageInstance = null;
    }
  }

  constructor() {}

  sendRoute(coords: Coords[]): void {
  
    this.mapPageInstance?.clearRoute();
    this.mapPageInstance?.drawRoute(coords);
  }

  sendPoints(coords: Coords[]): void {
    this.mapPageInstance?.drawPoints(coords);
  }

  sendGasStations(gasStations: GasStation[]): void {
    this.mapPageInstance?.markGasStations(gasStations);
  }

  sendWeather(weather: WeatherData[]): void {
    let data: WeatherData[] | null = weather && weather.length > 0 ? weather : null;
    this.mapPageInstance?.setWeatherData(data);
  }

  clearRoute(): void {
    this.mapPageInstance?.clearRoute();
  }

}