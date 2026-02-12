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

@Injectable({
  providedIn: 'root'
})
export class SearchBarService {

  constructor(private routeService: RouteService, private mapCommunication: MapCommunicationService, private userService: UserService, private authGuard: AuthGuard) { }

  onSubmit(routeFormResponse: RouteFormResponse): Observable<GasStation[]> {
    const observables = [
      this.saveCoordinates(routeFormResponse),
      this.saveWaypointCoordinates(routeFormResponse),
      this.saveGasStations(routeFormResponse),
      this.saveWeatherRoute(routeFormResponse)
    ];
    return forkJoin(observables).pipe(
      map((results) => {
        const [coordsRoute, coordsWaypoints, gasStations, weather] = results as [Coords[], Coords[], GasStation[], WeatherData[]];
        this.giveCoords(coordsRoute);
        this.giveWaypointCoords(coordsWaypoints);
        this.giveWeatherCoords(weather);
        return gasStations;
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