import { computed, inject, Injectable, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DefaultUserPreferences, Preferences, ThemeLangPreferences } from '../../Dto/preferences';
import { FavouriteGasStation } from '../../Dto/gas-station';
import { environment } from '../../../environments/environment';
import { UserSavedGasStationDto } from '../../Dto/user-saved-gas-station-dto';

@Injectable({
  providedIn: 'root'
})
export class UserPreferencesService {

  private baseUrl = environment.apiUrl;
  private http = inject(HttpClient);

  // Signals con valor inicial desde localStorage 
  favoriteGasStations = signal<FavouriteGasStation[]>(this.getInitial('favoriteGasStations', []));
  userPreferences = signal<any>(this.getInitial('userPreferences', {}));
  serverUserPreferences = signal<any>(this.getInitial('userPreferences', {}));
  themeLanguage = signal<ThemeLangPreferences>(this.getInitial('themeLanguage', {}));
  serverThemeLanguage = signal<ThemeLangPreferences>(this.getInitial('themeLanguage', {}));

  // Ver si el usuario ha realizado cambios. Se compara el signal interno con lo que sabemos que hay en BD
  readonly hasChanges = computed(() => {
    return JSON.stringify(this.userPreferences()) !== JSON.stringify(this.serverUserPreferences()) ||
      JSON.stringify(this.themeLanguage()) !== JSON.stringify(this.serverThemeLanguage());
  });

  // --- CATÁLOGOS
  fuelOptions = signal<string[]>(this.getInitial('fuelOptions', []));
  mapOptions = signal<string[]>(this.getInitial('mapOptions', []));
  themeOptions = signal<string[]>(this.getInitial('themeOptions', []));
  languageOptions = signal<string[]>(this.getInitial('languageOptions', []));
  gasStationBrandsOptions = signal<string[]>(this.getInitial('gasStationBrandsOptions', []));
  defaultPreferences = signal<DefaultUserPreferences | null>(this.getInitial('defaultPreferences', null));

  /**
   * 1. MAPEO DE COMBUSTIBLES
   * Centralizamos el mapeo para que ambos métodos lo usen
   */
  private fuelPropertyMap: Record<string, keyof FavouriteGasStation> = {
    'ALL': 'Gasolina95',
    'GASOLINE_95': 'Gasolina95',
    'GASOLINE_98': 'Gasolina98',
    'DIESEL': 'Diesel',
    'DIESEL_PREMIUM': 'DieselPremium',
    'GLP': 'GLP'
  };

  private getInitial(key: string, defaultValue: any) {
    const saved = localStorage.getItem(key);
    return saved ? JSON.parse(saved) : defaultValue;
  }

  /**
   * Método Pro: Actualiza la Signal y el Storage al mismo tiempo.
   * Úsalo tanto para preferencias como para llenar los catálogos en el initApp.
   */
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

  updateUserPreferences(
    radioKm: number,
    fuelType: string,
    maxPrice: number,
    mapType: string,
    avoidTolls: boolean,
    preferredBrands: string[]
  ): Observable<any> {

    const body = preferredBrands;

    return this.http.put(`${this.baseUrl}/api/users/preferences/update`, body, {
      params: {
        radioKm: radioKm.toString(),
        fuelType: fuelType,
        maxPrice: maxPrice.toString(),
        mapView: mapType,
        avoidTolls: avoidTolls.toString()
      },
      withCredentials: true
    });
  }

  updateUserThemeLanguage(theme: string, language: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/users/preferences/user/update`, null, {
      params: { theme, language },
      withCredentials: true
    });
  }

  updateFavouriteGasStations(favorite: FavouriteGasStation): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/users/favouriteStations`, favorite, {
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

  getUserFavouriteGasStations(): Observable<UserSavedGasStationDto[]> {
    return this.http.get<UserSavedGasStationDto[]>(`${this.baseUrl}/api/users/favouriteStations`, { withCredentials: true });
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

  /**
     * MÉTODO CENTRALIZADO: FILTRADO POR PRECIO MÁXIMO Y TIPO
     * Devuelve solo las estaciones que tienen el combustible disponible 
     * Y cuyo precio es menor o igual al máximo del usuario.
     */
  filteredFavoritesByMaxPrice = computed(() => {
    const stations = this.favoriteGasStations();
    const prefs = this.userPreferences();
    const maxPrice = prefs.maxPrice;
    const fuel = prefs.fuelType || 'ALL';

    const priceField = this.fuelPropertyMap[fuel] || 'Gasolina95';

    return stations.filter(station => {
      const price = station[priceField] as number | null;

      // Filtro: Debe tener precio (no ser null) Y ser menor o igual al máximo
      // Si el maxPrice es 0 o null, podrías decidir no filtrar o filtrar todo. 
      // Aquí asumimos que si hay maxPrice, se aplica.
      if (maxPrice > 0) {
        return price !== null && price <= maxPrice;
      }

      // Si no hay precio máximo definido, solo filtramos que tengan el combustible
      return price !== null;
    });
  });

  /**
   * MÉTODO CENTRALIZADO: ORDENACIÓN 
   */
  sortedFavoritesByPrice = computed(() => {
    const stations = [...this.favoriteGasStations()];
    const fuel = this.userPreferences().fuelType || 'ALL';
    const priceField = this.fuelPropertyMap[fuel] || 'Gasolina95';

    return stations.sort((a, b) => {
      const priceA = (a[priceField] as number) ?? Infinity;
      const priceB = (b[priceField] as number) ?? Infinity;
      return priceA - priceB;
    });
  });

  /**
   * FILTRADO POR MARCAS FAVORITAS
   * Devuelve solo las estaciones cuya marca coincide con las preferencias del usuario.
   * Si no hay marcas seleccionadas, devuelve todas las favoritas.
   */
  filteredFavoritesByBrand = computed(() => {
    const stations = this.favoriteGasStations();
    const preferredBrands = this.userPreferences().favoriteBrands || [];

    // Si el usuario no ha marcado ninguna marca como favorita, 
    // mostramos todas sus gasolineras guardadas.
    if (preferredBrands.length === 0) {
      return stations;
    }

    return stations.filter(station => {
      // Convertimos a mayúsculas para evitar problemas de "Repsol" vs "REPSOL"
      const stationBrand = station.marca?.toUpperCase().trim();
      console.log(stationBrand)

      // Comprobamos si la marca de la estación está en el array de favoritas
      return preferredBrands.some((brand: string) =>
        brand.toUpperCase().trim() === stationBrand
      );
    });
  });

}