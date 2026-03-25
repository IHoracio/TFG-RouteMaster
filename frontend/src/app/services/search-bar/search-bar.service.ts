import { Injectable } from '@angular/core';
import { RouteFormResponse } from '../../Dto/route-form-response';
import { RouteService } from '../routes/route.service';
import { Coords } from '../../Dto/maps-dtos';
import { MapCommunicationService } from '../map/map-communication.service';
import { WeatherData } from '../../Dto/weather-dtos';
import { Observable, map } from 'rxjs';
import { UserService } from '../user/user.service';
import { GasStation } from '../../Dto/gas-station';
import { FullRouteData } from '../../Dto/full-route-data';

@Injectable({
  providedIn: 'root'
})
export class SearchBarService {

  constructor(
    private routeService: RouteService, 
    private mapCommunication: MapCommunicationService, 
    private userService: UserService
  ) { }

  onSubmit(routeFormResponse: RouteFormResponse): Observable<GasStation[]> {
    console.log('🚀 Enviando formulario al Backend:', {
      origin: routeFormResponse.origin,
      destination: routeFormResponse.destination,
      waypoints: routeFormResponse.waypoints,
      optimize: routeFormResponse.optimizeRoute,
      avoidTolls: routeFormResponse.avoidTolls
    });
    return this.routeService.getFullRouteData(routeFormResponse).pipe(
      map((data: FullRouteData) => {
        this.giveCoords(data.polylineCoords);
        this.giveWaypointCoords(data.legCoords);
        this.giveWeatherCoords(data.weatherData);
        return data.gasStations; // Retornamos esto para la UI
      })
    );
  }

  saveUserData(): Observable<string> {
    return this.userService.receiveUserData();
  }

  saveFavouriteGasStations(): Observable<string> {
    return this.userService.receiveFavouriteGasStations();
  }

  saveSavedRoutes(): Observable<string> {
    return this.userService.receiveSavedRoutes();
  }

  saveFavouriteRoute(alias: string, routeFormResponse: RouteFormResponse, polylineCoords: Coords[], legCoords: Coords[], lang: string) {
    return this.routeService.saveFavouriteRoute(alias, routeFormResponse, polylineCoords, legCoords, lang);
  }

  giveCoords(coords: Coords[]) {
    this.mapCommunication.sendRoute(coords);
  }

  giveWaypointCoords(coords: Coords[]) {
    this.mapCommunication.sendPoints(coords);
  }

  giveGasStations(gasStations: GasStation[]) {
    this.mapCommunication.sendGasStations(gasStations);
  }

  giveWeatherCoords(weather: WeatherData[]) {
    this.mapCommunication.sendWeather(weather);
  }
}