import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RouteFormResponse } from '../../Dto/route-form-response';
import { GasStation } from '../../Dto/gas-station';
import { FullRouteData } from '../../Dto/full-route-data';
import { environment } from '../../../environments/environment';
import { Coords } from '../../Dto/maps-dtos';
import { TranslationService } from '../translation.service';

@Injectable({
  providedIn: 'root'
})
export class RouteService {
  private apiUrl = environment.apiUrl;
  private translation: TranslationService = inject(TranslationService);

  constructor(private http: HttpClient) { }

  shareRoute(
    polylineCoords: Coords[],
    legCoords: Coords[],
    gasRadius: number,
    lang: string
  ): Observable<{ url: string }> {
    const body = {
      polylineCoords,
      legCoords,
      gasRadius,
      lang
    };
    return this.http.post<{ url: string }>(`${this.apiUrl}/api/route/share`, body);
  }

  getSharedRoute(token: string): Observable<FullRouteData> {
    return this.http.get<FullRouteData>(`${this.apiUrl}/api/route/shared/${token}`);
  }

  getFullRouteData(routeFormResponse: RouteFormResponse): Observable<FullRouteData> {
    let parameters = new HttpParams()
      .set('origin', routeFormResponse.origin)
      .set('destination', routeFormResponse.destination)
      .set('optimizeWaypoints', routeFormResponse.optimizeWaypoints)
      .set('optimizeRoute', routeFormResponse.optimizeRoute)
      .set('language', this.translation.getCurrentLang ? this.translation.getCurrentLang() : 'es')
      .set('avoidTolls', routeFormResponse.avoidTolls)
      .set('gasRadius', routeFormResponse.radioKm || 1); 

    if (routeFormResponse.waypoints && routeFormResponse.waypoints.length > 0) {
      routeFormResponse.waypoints.forEach(wp => {
        parameters = parameters.append('waypoints', wp); 
      });
    }

    return this.http.get<FullRouteData>(`${this.apiUrl}/api/route/fullData`, { params: parameters });
  }

  getGasStationsByCoords(lat: number, lng: number, radio: number = 1): Observable<GasStation[]> {
    return this.http.get<GasStation[]>(`${this.apiUrl}/api/oil/gasolineras/radio/coords`, {
      params: { latitud: lat, longitud: lng, radio: radio },
      withCredentials: true
    });
  }

  saveFavouriteRoute(alias: string, routeFormResponse: RouteFormResponse, polylineCoords: Coords[], legCoords: Coords[], lang: string) {
    
    const puntosDTO: any[] = [];
    puntosDTO.push({ type: 'ORIGIN', address: routeFormResponse.origin });
    
    if (routeFormResponse.waypoints && routeFormResponse.waypoints.length > 0) {
      routeFormResponse.waypoints.forEach(wp => {
        puntosDTO.push({ type: 'WAYPOINT', address: wp });
      });
    }
    
    puntosDTO.push({ type: 'DESTINATION', address: routeFormResponse.destination });

    const body = {
      name: alias,
      puntosDTO: puntosDTO,
      polylineCoords: polylineCoords,
      legCoords: legCoords,
      gasRadius: routeFormResponse.radioKm || 1,
      language: lang
    };

    return this.http.post(`${this.apiUrl}/api/savedRoute/save`, body, { withCredentials: true });
  }
}