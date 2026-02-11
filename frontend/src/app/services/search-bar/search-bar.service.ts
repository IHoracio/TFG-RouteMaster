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

  onSubmit(routeFormResponse: RouteFormResponse, preferredBrands: string[], fuelType: string, maxPrice: number, radioKm: number): Observable<void> {
    const observables = [
      this.saveCoordinates(routeFormResponse),
      this.saveWaypointCoordinates(routeFormResponse),
      this.saveGasStationsCoordinates(routeFormResponse, preferredBrands, fuelType, maxPrice, radioKm),
      this.saveWeatherRoute(routeFormResponse)
    ];
    return forkJoin(observables).pipe(
      map((results) => {
        const [coordsRoute, coordsWaypoints, coordsGas, weather] = results as [Coords[], Coords[], Coords[], WeatherData[]];
        this.giveCoords(coordsRoute);
        this.giveWaypointCoords(coordsWaypoints);
        this.giveGasStationCoords(coordsGas);
        this.giveWeatherCoords(weather);
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

  saveGasStationsCoordinates(routeFormResponse: RouteFormResponse, preferredBrands: string[], fuelType: string, maxPrice: number, radioKm: number): Observable<Coords[]> {
    return this.authGuard.isLoggedIn().pipe(
      switchMap(logged => logged ? this.getFilteredGasStationCoords(routeFormResponse, preferredBrands, fuelType, maxPrice, radioKm) : this.getBasicGasStationCoords(routeFormResponse))
    );
  }

  private getFilteredGasStationCoords(routeFormResponse: RouteFormResponse, preferredBrands: string[], fuelType: string, maxPrice: number, radioKm: number): Observable<Coords[]> {
    return this.routeService.calculateGasStations(routeFormResponse).pipe(
      switchMap(coordsStr => {
        const coords: { lat: number, lng: number }[] = JSON.parse(coordsStr);
        const observables = coords.map(coord => this.routeService.getGasStationsByCoords(coord.lat, coord.lng, radioKm));
        return forkJoin(observables).pipe(
          map(results => {
            const allStations = results.flat();
            const filtered = this.filterGasStations(allStations, preferredBrands, fuelType, maxPrice);
            return filtered.map(station => ({ lat: station.latitud, lng: station.longitud }));
          })
        );
      })
    );
  }

  private getBasicGasStationCoords(routeFormResponse: RouteFormResponse): Observable<Coords[]> {
    return this.routeService.calculateGasStations(routeFormResponse).pipe(
      map(data => JSON.parse(data) as Coords[])
    );
  }

  private filterGasStations(stations: GasStation[], preferredBrands: string[], fuelType: string, maxPrice: number): GasStation[] {
    let filtered = stations;
    if (preferredBrands.length > 0) {
      filtered = filtered.filter(station =>
        preferredBrands.some(brand => brand.toLowerCase() === station.marca.toLowerCase())
      );
    }
    if (fuelType !== 'ELECTRIC') {
      const fuelKey = (fuelType === 'ALL' || fuelType === 'GASOLINE') ? 'Gasolina95' : 'Diesel';
      filtered = filtered.filter(station => {
        const price = station[fuelKey];
        return price != null && price <= maxPrice;
      });
    }
    return filtered;
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

  giveGasStationCoords(coords: Coords[]) {
    this.mapCommunication.sendGasStations(coords);
  }

  giveWeatherCoords(weather: WeatherData[]) {
    this.mapCommunication.sendWeather(weather);
  }
}