import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { RouteFormResponse } from '../../features/pages/map-page/Utils/route-form-response';
import { RouteGroupResponse } from '../../features/pages/map-page/Utils/google-route.mapper';
import { Coords } from '../../Dto/maps-dtos';


@Injectable({
  providedIn: 'root'
})
export class RouteService {

  private apiUrl = 'http://localhost:8080/routes';
  constructor(private http: HttpClient) {}

  calculateRoute(routeFormResponse: RouteFormResponse): Observable<RouteGroupResponse> {
    const headers = new HttpHeaders()
    .set('key', environment.googleMapsApiKey);

    const parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)

    return this.http.get(this.apiUrl, {headers: headers, params: parameters, responseType: 'json' });
  }


  calculateCoords(routeFormResponse: RouteFormResponse): Observable<string> {
    const headers = new HttpHeaders()
    .set('key', environment.googleMapsApiKey);

    const parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)


    return this.http.get(this.apiUrl + "/stepCoords", {headers: headers, params: parameters, responseType: 'text' });
  }
}
