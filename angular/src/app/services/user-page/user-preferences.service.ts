import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Preferences } from '../../Dto/preferences';
import { Municipalitie } from '../../Dto/municipalities';
import { GasStation, GasStationFavourite } from '../../Dto/gas-station';

@Injectable({
  providedIn: 'root'
})
export class UserPreferencesService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  getUserPreferences(userId: string, email: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/users/${userId}/preferences`, {
      params: { email }
    });
  }

  updateUserPreferences(userId: string, email: string, radioKm: number, fuelType: string, emissionType: string,  maxPrice: number, mapView: string, preferredBrands: string[], theme: string, language: string): Observable<any> {
    const body = { preferredBrands };
    return this.http.put(`${this.baseUrl}/api/users/${userId}/preferences`, body, {
      params: { email, radioKm: radioKm.toString(), fuelType, emissionType, maxPrice: maxPrice.toString(), mapView, theme, language }
    });
  }

  updateUserThemeLanguage(userId: string, email: string, theme: string, language: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/users/${userId}/preferences/user`, {}, {
      params: { email, theme, language }
    });
  }

  updateGasStationFavourites(email: string, alias: string, idEstacion: number): Observable<GasStationFavourite[]> {
    return this.http.put<GasStationFavourite[]>(`${this.baseUrl}/api/users/favourites/${idEstacion}`, {}, {
      params: { email, alias, idEstacion }
    });
  }

  deleteGasStationFavourites(email: string, alias: string, idEstacion: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/users/favourites/${idEstacion}`, {
      params: { email, alias }
    });
  }

  getGasStationBrands(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/api/oil/gasolineras/marcas`);
  }

  getUserGasStationFavourites(email: string): Observable<GasStationFavourite[]> {
    return this.http.get<GasStationFavourite[]>(`${this.baseUrl}/api/users/favourites`,{
      params: {email}
    });
  }

  getGasStation(idEstacion: number): Observable<GasStation> {
    return this.http.get<GasStation>(`${this.baseUrl}/api/oil/id/${idEstacion}`);
  }

  getMapTypes(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/route-options/map-types`);
  }

  getFuelTypes(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/route-options/fuels`);
  }

  getEmissionLabels(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/route-options/emissions`);
  }

  getThemes(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/preferences/themes`);
  }

  getLanguages(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/preferences/languages`);
  }

  getMunicipalities(): Observable<Municipalitie[]>{
    return this.http.get<Municipalitie[]>(`${this.baseUrl}/api/oil/municipios`);
  }
}