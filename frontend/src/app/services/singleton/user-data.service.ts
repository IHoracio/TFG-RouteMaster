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
            langs: this.userPrefsService.getLanguages()
        }).pipe(
            tap(data => {
                // Distribuimos a los Signals
                this.userInfoService.setUser(data.info);
                this.userPrefsService.userPreferences.set(data.prefs); // Aquí podrías usar tu método updateData
                this.userPrefsService.favoriteGasStations.set(data.favs);
                this.userPrefsService.fuelOptions.set(data.fuels.map((f: any) => f.code));
                this.userPrefsService.mapOptions.set(data.maps.map((f: any) => f.code));
                this.userPrefsService.defaultPreferences.set(data.defaults);
                this.userPrefsService.gasStationBrandsOptions.set(data.brands);
                this.userInfoService.setRoutes(data.routes);
                this.userPrefsService.themeOptions.set(data.themes.map(t => t.code));
                this.userPrefsService.languageOptions.set(data.langs.map(l => l.code));
                // ... etc
            }),
            map(() => true),
            catchError(() => of(false)) // Si algo falla, la app sigue pero marcada como "error"
        );
    }

}