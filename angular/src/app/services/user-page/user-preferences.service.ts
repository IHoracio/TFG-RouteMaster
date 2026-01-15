import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DefaultUserPreferences, Preferences, ThemeLangPreferences } from '../../Dto/preferences';
import { Municipalitie } from '../../Dto/municipalities';
import { GasStation, FavouriteGasStation } from '../../Dto/gas-station';

@Injectable({
  providedIn: 'root'
})
export class UserPreferencesService {
  private baseUrl = 'http://localhost:8080';

  private favoriteGasStationsSignal = signal<FavouriteGasStation[]>([]);


  constructor(private http: HttpClient) { }

  getFavoriteGasStationsSignal() { return this.favoriteGasStationsSignal; }
  setFavoriteGasStations(data: FavouriteGasStation[]) { this.favoriteGasStationsSignal.set(data); }

  getUserPreferences(email: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/users/0/preferences`, {
      params: { email }
    });
  }

  getDefaultPreferences(): Observable<DefaultUserPreferences> {
    return this.http.get<DefaultUserPreferences>(`${this.baseUrl}/api/users/defaultPreferences`);
  }

  getUserThemeLanguage(email: string): Observable<ThemeLangPreferences> {
    return this.http.get<ThemeLangPreferences>(`${this.baseUrl}/api/users/0/preferences/user`, {
      params: { email }
    });
  }

  updateUserPreferences(email: string, radioKm: number, fuelType: string, emissionType: string, maxPrice: number, mapType: string, avoidTolls: boolean, preferredBrands: string[]): Observable<any> {
    const body = { preferredBrands };
    return this.http.put(`${this.baseUrl}/api/users/0/preferences`, body, {
      params: { email, radioKm: radioKm.toString(), fuelType, vehicleEmissionType: emissionType, maxPrice: maxPrice.toString(), mapView: mapType, avoidTolls }
    });
  }

  updateUserThemeLanguage(email: string, theme: string, language: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/users/0/preferences/user`, null, {
      params: { email, theme, language }
    });
  }

  updateFavouriteGasStations(email: string, alias: string, idEstacion: number): Observable<FavouriteGasStation[]> {
    return this.http.put<FavouriteGasStation[]>(`${this.baseUrl}/api/users/favourites/${idEstacion}`, {}, {
      params: { email, alias, idEstacion }
    });
  }

  deleteFavouriteGasStations(email: string, alias: string, idEstacion: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/users/favourites/${idEstacion}`, {
      params: { email, alias }
    });
  }

  getGasStationBrands(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/api/oil/gasolineras/marcas`);
  }

  getUserFavouriteGasStations(email: string): Observable<FavouriteGasStation[]> {
    return this.http.get<FavouriteGasStation[]>(`${this.baseUrl}/api/users/favourites`, {
      params: { email }
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

  getMunicipalities(): Observable<Municipalitie[]> {
    return this.http.get<Municipalitie[]>(`${this.baseUrl}/api/oil/municipios`);
  }
}