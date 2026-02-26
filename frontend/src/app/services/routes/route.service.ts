import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { RouteFormResponse } from '../../Dto/route-form-response';
import { GasStation } from '../../Dto/gas-station';
import { FullRouteData } from '../../Dto/full-route-data';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  private apiUrl = environment.apiUrl;
  private routeUrl = '/api/savedRoute';

  constructor(private http: HttpClient) { }

  getFullRouteData(routeFormResponse: RouteFormResponse): Observable<FullRouteData> {

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
      .set('language', 'es')
      .set('avoidTolls', routeFormResponse.avoidTolls)
      .set('gasRadius', routeFormResponse.radioKm || 1); 

    return this.http.get<FullRouteData>(this.apiUrl + "/api/route/fullData", { params: parameters });
  }

  calculateRoute(routeFormResponse: RouteFormResponse): Observable<string> {

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute);

    return this.http.get(this.apiUrl, { params: parameters, responseType: 'text' });
  }

  calculatePolylineCoords(routeFormResponse: RouteFormResponse): Observable<string> {
    return this.getFullRouteData(routeFormResponse).pipe(
      map(data => JSON.stringify(data.polylineCoords))
    );
  }

  calculatePointCoords(routeFormResponse: RouteFormResponse): Observable<string> {
    return this.getFullRouteData(routeFormResponse).pipe(
      map(data => JSON.stringify(data.legCoords))
    );
  }

  calculateGasStations(routeFormResponse: RouteFormResponse): Observable<string> {
    return this.getFullRouteData(routeFormResponse).pipe(
      map(data => JSON.stringify(data.gasStations)) 
    );
  }

  getGasStationsByCoords(lat: number, lng: number, radio: number = 1): Observable<GasStation[]> {
    return this.http.get<GasStation[]>(`${this.apiUrl}/api/oil/gasolineras/radio/coords`, {
      params: { latitud: lat, longitud: lng, radio: radio },
      withCredentials: true
    });
  }

  calculateWeatherRoute(routeFormResponse: RouteFormResponse): Observable<string> {
    return this.getFullRouteData(routeFormResponse).pipe(
      map(data => JSON.stringify(data.weatherData))
    );
  }

  saveFavouriteRoute(alias: string, routeFormResponse: RouteFormResponse) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    let waypointsString = routeFormResponse.waypoints.join('|');
    let params = new HttpParams()
      .set('name', alias)
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute);

    console.log(params);
    return this.http.post(this.apiUrl +
      this.routeUrl + '/save',
      null,
      { params, withCredentials: true }
    );
  }
}