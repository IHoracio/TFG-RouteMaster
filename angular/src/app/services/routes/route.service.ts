import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { RouteFormResponse } from '../../features/pages/map-page/Utils/route-form-response';
import { RouteGroupResponse } from '../../Dto/maps-dtos';


@Injectable({
  providedIn: 'root'
})
export class RouteService {

  private apiUrl = 'http://localhost:8080/routes';
  constructor(private http: HttpClient) {}

  calculateRoute(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders()
    .set('key', environment.googleMapsApiKey);
    let waypointsString = routeFormResponse.waypoints.join('|');
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('waypoints', waypointsString)
      
    parameters = this.assignWaypoints(parameters, routeFormResponse.waypoints)
    return this.http.get(this.apiUrl, {headers: headers, params: parameters, responseType: 'text' });
  }


  calculateCoords(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders()
    .set('key', environment.googleMapsApiKey);

    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
    
      
    parameters = this.assignWaypoints(parameters, routeFormResponse.waypoints)
    return this.http.get(this.apiUrl + "/stepCoords", {headers: headers, params: parameters, responseType: 'text' });
  }

  assignWaypoints(parameters: HttpParams, waypoints: string[]){
    waypoints.forEach((waypoint)=>{
      parameters.set('waypoints', waypoint[0])
    })
    return parameters;
  }
}
