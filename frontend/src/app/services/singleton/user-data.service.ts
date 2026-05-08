import { Injectable, inject } from '@angular/core';
import { catchError, forkJoin, map, Observable, of, tap } from 'rxjs';
import { UserInfoService } from '../user-page/user-info.service';
import { UserPreferencesService } from '../user-page/user-preferences.service';
import { GasStationService } from '../user-page/gas-station/gas-station.service';

@Injectable({
    providedIn: 'root'
})
export class UserDataService {
    private userInfoService = inject(UserInfoService);
    private userPrefsService = inject(UserPreferencesService);
    private gasService = inject(GasStationService);

    /**
   * Este método será el UNICO que se llame al iniciar la app.
   * Carga todo en paralelo y distribuye los resultados.
   */
    initApp(): Observable<boolean> {
        return forkJoin({
            info: this.userInfoService.getUserInfo(),
            prefs: this.userPrefsService.getUserPreferences(),
            favs: this.userPrefsService.getUserFavouriteGasStations(),
            defaults: this.userPrefsService.getDefaultPreferences(),
            fuels: this.userPrefsService.getFuelTypes(),
            maps: this.userPrefsService.getMapTypes(),
            brands: this.gasService.getGasStationBrands(),
            routes: this.userInfoService.getUserRoutes(),
            themes: this.userPrefsService.getThemes(),
            langs: this.userPrefsService.getLanguages(),
            themeLang: this.userPrefsService.getUserThemeLanguage()
        }).pipe(
            tap(data => {
                // Para los datos privados
                this.userInfoService.setUser(data.info);
                this.userInfoService.setRoutes(data.routes);

                // Para los catálogos y preferencias
                // Usamos updateData (o creamos uno similar) para asegurar la persistencia
                this.userPrefsService.updateData(this.userPrefsService.userPreferences, 'userPreferences', data.prefs);
                this.userPrefsService.updateData(this.userPrefsService.serverUserPreferences, 'userPreferences', data.prefs);
                this.userPrefsService.updateData(this.userPrefsService.defaultPreferences, 'defaultPreferences', data.defaults);

                // === THEME + LANGUAGE ===
                this.userPrefsService.updateData(this.userPrefsService.serverThemeLanguage, 'themeLanguage', data.themeLang);
                this.userPrefsService.updateData(this.userPrefsService.themeLanguage, 'themeLanguage', data.themeLang);

                // === GASOLINERAS FAVORITAS (Añadido para persistencia) ===
                this.userPrefsService.updateData(this.userPrefsService.favoriteGasStations, 'favoriteGasStations', data.favs);

                // Para las listas (puedes crear un helper o hacerlo manual)
                const fuelCodes = data.fuels.map((f: any) => f.code);
                this.userPrefsService.updateData(this.userPrefsService.fuelOptions, 'fuelOptions', fuelCodes);

                const mapCodes = data.maps.map((f: any) => f.code);
                this.userPrefsService.updateData(this.userPrefsService.mapOptions, 'mapOptions', mapCodes);

                const themeCodes = data.themes.map((t: any) => t.code);
                this.userPrefsService.updateData(this.userPrefsService.themeOptions, 'themeOptions', themeCodes);

                const langCodes = data.langs.map((l: any) => l.code);
                this.userPrefsService.updateData(this.userPrefsService.languageOptions, 'languageOptions', langCodes);

                this.userPrefsService.updateData(this.userPrefsService.gasStationBrandsOptions, 'gasStationBrandsOptions', data.brands);
            }),
            map(() => true),
            catchError(() => of(false)) // Si algo falla, la app sigue pero marcada como "error"
        );
    }

}