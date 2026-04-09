import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DefaultUserPreferences, Preferences, ThemeLangPreferences } from '../../Dto/preferences';
import { FavouriteGasStation } from '../../Dto/gas-station';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserPreferencesService {

  private baseUrl = environment.apiUrl;
  private http = inject(HttpClient);

  // Signals con valor inicial desde localStorage (si existe)
  favoriteGasStations = signal<FavouriteGasStation[]>(this.getInitial('favoriteGasStations', []));
  userPreferences = signal<any>(this.getInitial('userPreferences', {}));
  themeLanguage = signal<any>(this.getInitial('themeLanguage', {}));

  // Catálogos (No necesitan persistencia en localStorage, se cargan al inicio)
  fuelOptions = signal<string[]>([]);
  themeOptions = signal<string[]>([]);
  languageOptions = signal<string[]>([]);
  gasStationBrandsOptions = signal<string[]>([]);

  private getInitial(key: string, defaultValue: any) {
    const saved = localStorage.getItem(key);
    return saved ? JSON.parse(saved) : defaultValue;
  }

  // Métodos de actualización: Actualizan el Signal y el Storage
  updateData<T>(sig: WritableSignal<T>, key: string, data: T) {
    sig.set(data);
    localStorage.setItem(key, JSON.stringify(data));
  }

  getUserPreferences(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/users/preferences/get`, { withCredentials: true });
  }

  getDefaultPreferences(): Observable<DefaultUserPreferences> {
    return this.http.get<DefaultUserPreferences>(`${this.baseUrl}/api/users/preferences/default`, { withCredentials: true });
  }

  getUserThemeLanguage(): Observable<ThemeLangPreferences> {
    return this.http.get<ThemeLangPreferences>(`${this.baseUrl}/api/users/preferences/user/get`, { withCredentials: true });
  }

  updateUserPreferences(radioKm: number, fuelType: string, maxPrice: number, mapType: string, avoidTolls: boolean, preferredBrands: string[]): Observable<any> {
    const body = { preferredBrands };
    return this.http.put(`${this.baseUrl}/api/users/preferences/update`, body, {
      params: { radioKm: radioKm.toString(), fuelType: fuelType, maxPrice: maxPrice.toString(), mapView: mapType, avoidTolls: avoidTolls.toString() },
      withCredentials: true
    });
  }

  updateUserThemeLanguage(theme: string, language: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/users/preferences/user/update`, null, {
      params: { theme, language },
      withCredentials: true
    });
  }

  updateFavouriteGasStations(alias: string, idEstacion: number): Observable<FavouriteGasStation[]> {
    return this.http.put<FavouriteGasStation[]>(`${this.baseUrl}/api/users/favouriteStations`, {}, {
      params: { alias, idEstacion: idEstacion.toString() },
      withCredentials: true
    });
  }

  deleteFavouriteGasStations(alias: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/users/favouriteStations`, {
      params: { alias },
      withCredentials: true
    });
  }

  renameFavouriteGasStations(oldAlias: string, newAlias: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/users/favouriteStations`, null, {
      params: { oldAlias, newAlias },
      withCredentials: true
    });
  }

  getUserFavouriteGasStations(): Observable<FavouriteGasStation[]> {
    return this.http.get<FavouriteGasStation[]>(`${this.baseUrl}/api/users/favouriteStations`, { withCredentials: true });
  }

  getMapTypes(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/route-options/map-types`, { withCredentials: true });
  }

  getFuelTypes(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/route-options/fuels`, { withCredentials: true });
  }

  getThemes(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/preferences/themes`, { withCredentials: true });
  }

  getLanguages(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/preferences/languages`, { withCredentials: true });
  }

  clearUserData() {
    // 1. Resetear Signals a sus valores iniciales
    this.userPreferences.set({});
    this.favoriteGasStations.set([]);
    this.fuelOptions.set([]);
    this.themeLanguage.set({});
    this.themeOptions.set([]);
    this.languageOptions.set([]);
    this.gasStationBrandsOptions.set([]);

    // 2. Limpiar el almacenamiento físico
    localStorage.clear();
  }

}