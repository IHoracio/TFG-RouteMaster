import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DefaultUserPreferences, Preferences, ThemeLangPreferences } from '../../Dto/preferences';
import { Municipalitie } from '../../Dto/municipalities';
import { GasStation, FavouriteGasStation } from '../../Dto/gas-station';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserPreferencesService {
  private baseUrl = environment.apiUrl;

  private favoriteGasStationsSignal = signal<FavouriteGasStation[]>([]);
  private userPreferencesSignal = signal<any>({});
  private defaultPreferencesSignal = signal<any>(null);
  private themeLanguageSignal = signal<any>({});
  private fuelOptionsSignal = signal<string[]>([]);
  private themeOptionsSignal = signal<string[]>([]);
  private languageOptionsSignal = signal<string[]>([]);
  private mapTypeOptionsSignal = signal<string[]>([]);
  private emissionLabelOptionsSignal = signal<string[]>([]);
  private gasStationBrandsOptionsSignal = signal<string[]>([]);
  private spainMunicipalitiesSignal = signal<string[]>([]);

  constructor(private http: HttpClient) {
    const savedFavStations = localStorage.getItem('favoriteGasStations');
    if (savedFavStations) {
      this.favoriteGasStationsSignal.set(JSON.parse(savedFavStations));
    }
    const savedPrefs = localStorage.getItem('userPreferences');
    if (savedPrefs) {
      this.userPreferencesSignal.set(JSON.parse(savedPrefs));
    }
    const savedDefaults = localStorage.getItem('defaultPreferences');
    if (savedDefaults) {
      this.defaultPreferencesSignal.set(JSON.parse(savedDefaults));
    }
    const savedThemeLang = localStorage.getItem('themeLanguage');
    if (savedThemeLang) {
      this.themeLanguageSignal.set(JSON.parse(savedThemeLang));
    }
    const savedFuelOptions = localStorage.getItem('fuelOptions');
    if (savedFuelOptions) {
      this.fuelOptionsSignal.set(JSON.parse(savedFuelOptions));
    }
    const savedThemeOptions = localStorage.getItem('themeOptions');
    if (savedThemeOptions) {
      this.themeOptionsSignal.set(JSON.parse(savedThemeOptions));
    }
    const savedLanguageOptions = localStorage.getItem('languageOptions');
    if (savedLanguageOptions) {
      this.languageOptionsSignal.set(JSON.parse(savedLanguageOptions));
    }
    const savedMapTypeOptions = localStorage.getItem('mapTypeOptions');
    if (savedMapTypeOptions) {
      this.mapTypeOptionsSignal.set(JSON.parse(savedMapTypeOptions));
    }
    const savedEmissionLabelOptions = localStorage.getItem('emissionLabelOptions');
    if (savedEmissionLabelOptions) {
      this.emissionLabelOptionsSignal.set(JSON.parse(savedEmissionLabelOptions));
    }
    const savedGasStationBrandsOptions = localStorage.getItem('gasStationBrandsOptions');
    if (savedGasStationBrandsOptions) {
      this.gasStationBrandsOptionsSignal.set(JSON.parse(savedGasStationBrandsOptions));
    }
    const savedSpainMunicipalities = localStorage.getItem('spainMunicipalities');
    if (savedSpainMunicipalities) {
      this.spainMunicipalitiesSignal.set(JSON.parse(savedSpainMunicipalities));
    }
  }

  getFavoriteGasStationsSignal() { return this.favoriteGasStationsSignal; }
  setFavoriteGasStations(data: FavouriteGasStation[]) { 
    this.favoriteGasStationsSignal.set(data); 
    localStorage.setItem('favoriteGasStations', JSON.stringify(data));
  }
  updateFavoriteGasStationsSignal(updater: (stations: FavouriteGasStation[]) => FavouriteGasStation[]) {
    this.favoriteGasStationsSignal.update(updater);
    localStorage.setItem('favoriteGasStations', JSON.stringify(this.favoriteGasStationsSignal()));
  }

  getUserPreferencesSignal() { return this.userPreferencesSignal; }
  setUserPreferences(data: any) { 
    this.userPreferencesSignal.set(data); 
    localStorage.setItem('userPreferences', JSON.stringify(data));
  }

  getDefaultPreferencesSignal() { return this.defaultPreferencesSignal; }
  setDefaultPreferences(data: any) { 
    this.defaultPreferencesSignal.set(data); 
    localStorage.setItem('defaultPreferences', JSON.stringify(data));
  }

  getThemeLanguageSignal() { return this.themeLanguageSignal; }
  setThemeLanguage(data: any) { 
    this.themeLanguageSignal.set(data); 
    localStorage.setItem('themeLanguage', JSON.stringify(data));
  }

  getFuelOptionsSignal() { return this.fuelOptionsSignal; }
  setFuelOptions(data: string[]) { 
    this.fuelOptionsSignal.set(data); 
    localStorage.setItem('fuelOptions', JSON.stringify(data));
  }

  getThemeOptionsSignal() { return this.themeOptionsSignal; }
  setThemeOptions(data: string[]) { 
    this.themeOptionsSignal.set(data); 
    localStorage.setItem('themeOptions', JSON.stringify(data));
  }

  getLanguageOptionsSignal() { return this.languageOptionsSignal; }
  setLanguageOptions(data: string[]) { 
    this.languageOptionsSignal.set(data); 
    localStorage.setItem('languageOptions', JSON.stringify(data));
  }

  getMapTypeOptionsSignal() { return this.mapTypeOptionsSignal; }
  setMapTypeOptions(data: string[]) { 
    this.mapTypeOptionsSignal.set(data); 
    localStorage.setItem('mapTypeOptions', JSON.stringify(data));
  }

  getEmissionLabelOptionsSignal() { return this.emissionLabelOptionsSignal; }
  setEmissionLabelOptions(data: string[]) { 
    this.emissionLabelOptionsSignal.set(data); 
    localStorage.setItem('emissionLabelOptions', JSON.stringify(data));
  }

  getGasStationBrandsOptionsSignal() { return this.gasStationBrandsOptionsSignal; }
  setGasStationBrandsOptions(data: string[]) { 
    this.gasStationBrandsOptionsSignal.set(data); 
    localStorage.setItem('gasStationBrandsOptions', JSON.stringify(data));
  }

  getSpainMunicipalitiesSignal() { return this.spainMunicipalitiesSignal; }
  setSpainMunicipalities(data: string[]) { 
    this.spainMunicipalitiesSignal.set(data); 
    localStorage.setItem('spainMunicipalities', JSON.stringify(data));
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

  updateUserPreferences(radioKm: number, fuelType: string, emissionType: string, maxPrice: number, mapType: string, avoidTolls: boolean, preferredBrands: string[]): Observable<any> {
    const body = { preferredBrands };
    return this.http.put(`${this.baseUrl}/api/users/preferences/update`, body, {
      params: { radioKm: radioKm.toString(), fuelType, vehicleEmissionType: emissionType, maxPrice: maxPrice.toString(), mapView: mapType, avoidTolls: avoidTolls.toString() },
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

  getEmissionLabels(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/route-options/emissions`, { withCredentials: true });
  }

  getThemes(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/preferences/themes`, { withCredentials: true });
  }

  getLanguages(): Observable<Preferences[]> {
    return this.http.get<Preferences[]>(`${this.baseUrl}/api/preferences/languages`, { withCredentials: true });
  }
}