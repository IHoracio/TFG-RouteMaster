import { Injectable, TemplateRef } from '@angular/core';
import { RouteFormResponse } from '../../Dto/route-form-response';
import { RouteService } from '../routes/route.service';
import { Coords } from '../../Dto/maps-dtos';
import { MapCommunicationService } from '../map/map-communication.service';
import { WeatherData } from '../../Dto/weather-dtos';
import { Observable, forkJoin, map, switchMap } from 'rxjs';
import { UserService } from '../user/user.service';
import { AuthGuard } from '../../guards/auth.guard';
import { GasStation } from '../../Dto/gas-station';
import { FullRouteData } from '../../Dto/full-route-data';

@Injectable({
  providedIn: 'root'
})
export class SearchBarService {

  constructor(private routeService: RouteService, private mapCommunication: MapCommunicationService, private userService: UserService, private authGuard: AuthGuard) { }

  onSubmit(routeFormResponse: RouteFormResponse): Observable<GasStation[]> {
    return this.routeService.getFullRouteData(routeFormResponse).pipe(
      map((data: FullRouteData) => {
        this.giveCoords(data.polylineCoords);
        this.giveWaypointCoords(data.legCoords);
        this.giveWeatherCoords(data.weatherData);
        return data.gasStations;
      })
    );
  }

  saveCoordinates(routeFormResponse: RouteFormResponse): Observable<Coords[]> {
    return this.routeService.calculatePolylineCoords(routeFormResponse).pipe(
      map(data => JSON.parse(data) as Coords[])
    );
  }

  saveWaypointCoordinates(routeFormResponse: RouteFormResponse): Observable<Coords[]> {
    return this.routeService.calculatePointCoords(routeFormResponse).pipe(
      map(data => JSON.parse(data) as Coords[])
    );
  }

  saveGasStations(routeFormResponse: RouteFormResponse): Observable<GasStation[]> {
    return this.routeService.calculateGasStations(routeFormResponse).pipe(
      map(data => JSON.parse(data) as GasStation[])
    );
  }

  saveWeatherRoute(routeFormResponse: RouteFormResponse): Observable<WeatherData[]> {
    return this.routeService.calculateWeatherRoute(routeFormResponse).pipe(
      map(data => JSON.parse(data) as WeatherData[])
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

  saveFavouriteRoute(alias: string, routeFormResponse: RouteFormResponse) {
    return this.routeService.saveFavouriteRoute(alias, routeFormResponse);
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