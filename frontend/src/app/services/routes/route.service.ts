import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RouteFormResponse } from '../../Dto/route-form-response';
import { GasStation } from '../../Dto/gas-station';
import { FullRouteData, PuntosDTO } from '../../Dto/full-route-data';
import { environment } from '../../../environments/environment';
import { Coords } from '../../Dto/maps-dtos';
import { TranslationService } from '../singleton/translation.service';

@Injectable({
  providedIn: 'root'
})
export class RouteService {
  private apiUrl = environment.apiUrl;
  private translation: TranslationService = inject(TranslationService);

  constructor(private http: HttpClient) { }

  shareRoute(
    totalDistance: string,
    totalDuration: string,
    puntosDTO: PuntosDTO[],
    polylineCoords: Coords[],
    legCoords: Coords[],
    gasRadius: number,
    lang: string
  ): Observable<{ url: string }> {
    const body = {
      totalDistance,
      totalDuration,
      puntosDTO: puntosDTO,
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
    const body = {
      ...routeFormResponse,
      language: this.translation.getCurrentLang ? this.translation.getCurrentLang().toLocaleLowerCase() : 'es',
      gasRadius: routeFormResponse.radioKm || 1
    };

    return this.http.post<FullRouteData>(`${this.apiUrl}/api/route/fullData`, body);
  }

  getGasStationsByCoords(lat: number, lng: number, radio: number = 1): Observable<GasStation[]> {
    return this.http.get<GasStation[]>(`${this.apiUrl}/api/oil/gasolineras/radio/coords`, {
      params: { latitud: lat, longitud: lng, radio: radio },
      withCredentials: true
    });
  }

saveFavouriteRoute(
  alias: string,
  routeFormResponse: RouteFormResponse,
  polylineCoords: Coords[],
  legCoords: Coords[],
  lang: string,
  totalDistance: string,
  totalDuration: string
) {
  const puntosDTO: any[] = [];

  // CAMBIO CLAVE: Enviamos 'placeSelection' en lugar de 'address'
  // y pasamos el objeto completo (routeFormResponse.origin ya es de tipo PlaceSelection)
  puntosDTO.push({ 
    type: 'ORIGIN', 
    placeSelection: routeFormResponse.origin 
  });

  if (routeFormResponse.waypoints && routeFormResponse.waypoints.length > 0) {
    routeFormResponse.waypoints.forEach(wp => {
      if (wp) { // Validamos que el waypoint no sea null
        puntosDTO.push({ 
          type: 'WAYPOINT', 
          placeSelection: wp 
        });
      }
    });
  }

  puntosDTO.push({ 
    type: 'DESTINATION', 
    placeSelection: routeFormResponse.destination 
  });

  const body = {
    name: alias,
    puntosDTO: puntosDTO,
    polylineCoords: polylineCoords,
    legCoords: legCoords,
    gasRadius: routeFormResponse.radioKm || 1,
    language: lang,
    totalDistance: totalDistance,
    totalDuration: totalDuration
  };

  return this.http.post(`${this.apiUrl}/api/savedRoute/save`, body, { withCredentials: true });
}

  /**
 * Llama al backend para ejecutar (recuperar trazado y datos) una ruta guardada
 * @param routeId El UUID de la ruta guardada
 */
  executeSavedRoute(routeId: string): Observable<FullRouteData> {
    return this.http.get<FullRouteData>(
      `${this.apiUrl}/api/savedRoute/execute/${routeId}`,
      { withCredentials: true }
    );
  }
}