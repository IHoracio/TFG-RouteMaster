import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { RouteFormResponse } from '../../features/pages/map-page/Utils/route-form-response';
import { Coords, RouteGroupResponse } from '../../Dto/maps-dtos';


@Injectable({
  providedIn: 'root'
})
export class RouteService {

  private apiUrl = 'http://localhost:8080/api/routes';
  constructor(private http: HttpClient) {}

  calculateRoute(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
      

    return this.http.get(this.apiUrl, {headers: headers, params: parameters, responseType: 'text' });
  }


  calculatePolylineCoords(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
    return this.http.get(this.apiUrl + "/polylineCoords", {headers: headers, params: parameters, responseType: 'text' });
  }

  calculatePointCoords(routeFormResponse: RouteFormResponse): Observable<string>{
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)

    return this.http.get(this.apiUrl + "/legCoords", {headers: headers, params: parameters, responseType: 'text' });
  }

  calculateGasStations(routeFormResponse: RouteFormResponse): Observable<string>{
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);

    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
      .set('radius', 5)

    return this.http.get(this.apiUrl + "/gasStations", {headers: headers, params: parameters, responseType: 'text' });
  }
}
