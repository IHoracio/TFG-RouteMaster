import { Injectable, inject } from '@angular/core';
import { forkJoin } from 'rxjs';
import { UserInfoService } from '../user-page/user-info.service';
import { UserPreferencesService } from '../user-page/user-preferences.service';
import { GasStationService } from '../user-page/gas-station/gas-station.service';
import { FavouriteGasStation, GasStation } from '../../Dto/gas-station';

@Injectable({
    providedIn: 'root'
})
export class UserDataService {
    private userInfoService = inject(UserInfoService);
    private userPreferencesService = inject(UserPreferencesService);
    private gasStationService = inject(GasStationService);

    loadInitialData(): void {
        this.userInfoService.getUserInfo().subscribe({
            next: (data) => this.userInfoService.setUser({ email: data.email || 'N/A', name: data.name || 'N/A', surname: data.surname || 'N/A' }),
            error: (err) => { console.error('Error user info:', err); this.userInfoService.setUser({ email: 'Error', name: 'Error', surname: 'Error' }); }
        });

        this.userPreferencesService.getUserPreferences().subscribe({
            next: (data) => this.userPreferencesService.setUserPreferences(data || {}),
            error: (err) => console.error('Error preferences:', err)
        });

        this.userPreferencesService.getDefaultPreferences().subscribe({
            next: (data) => this.userPreferencesService.setDefaultPreferences(data),
            error: (err) => console.error('Error defaults:', err)
        });

        this.userPreferencesService.getUserThemeLanguage().subscribe({
            next: (data) => this.userPreferencesService.setThemeLanguage(data),
            error: (err) => console.error('Error theme/language:', err)
        });

        this.userPreferencesService.getUserFavouriteGasStations().subscribe({
            next: (data: FavouriteGasStation[]) => {
                this.userPreferencesService.setFavoriteGasStations(data || []);
                this.userPreferencesService.getFavoriteGasStationsSignal()().forEach((favorite: FavouriteGasStation) => {
                    if (!favorite.latitud || !favorite.longitud) {
                        this.gasStationService.getGasStation(favorite.idEstacion).subscribe({
                            next: (fullStation: GasStation) => {
                                this.userPreferencesService.updateFavoriteGasStationsSignal(stations =>
                                    stations.map(s => s.idEstacion === favorite.idEstacion ? { ...fullStation, alias: favorite.alias } as FavouriteGasStation : s)
                                );
                            },
                            error: (err) => console.error('Error gas station:', err)
                        });
                    }
                });
            },
            error: (err) => console.error('Error favorite gas stations:', err)
        });

        this.userInfoService.getUserRoutes().subscribe({
            next: (data: any[]) => {
                this.userInfoService.setRoutes(data || []);
                if (data && data.length > 0) {
                    const executeCalls = data.map(route => this.userInfoService.executeRoute(route.routeId));
                    forkJoin(executeCalls).subscribe({
                        next: (results) => {
                            this.userInfoService.setRoutes(this.userInfoService.getRoutesSignal()().map((route, index) => {
                                const res = results[index];
                                const distanceKm = (res.distanceMeters / 1000).toFixed(2);
                                const hours = Math.floor(res.durationSeconds / 3600);
                                const minutes = Math.floor((res.durationSeconds % 3600) / 60);
                                return { ...route, distanceKm, durationFormatted: `${hours}h ${minutes}m` };
                            }));
                        },
                        error: (err) => console.error('Error executing routes:', err)
                    });
                }
            },
            error: (err) => console.error('Error routes:', err)
        });

        this.userPreferencesService.getFuelTypes().subscribe(options => this.userPreferencesService.setFuelOptions(options?.map(p => p.code) || []));
        this.userPreferencesService.getThemes().subscribe(options => this.userPreferencesService.setThemeOptions(options?.map(p => p.code) || []));
        this.userPreferencesService.getLanguages().subscribe(options => this.userPreferencesService.setLanguageOptions(options?.map(p => p.code) || []));
        this.gasStationService.getGasStationBrands().subscribe(options => this.userPreferencesService.setGasStationBrandsOptions(options || []));
        this.userPreferencesService.getMapTypes().subscribe(options => this.userPreferencesService.setMapTypeOptions(options?.map(p => p.code) || []));
        this.userPreferencesService.getEmissionLabels().subscribe(options => this.userPreferencesService.setEmissionLabelOptions(options?.map(p => p.code) || []));
        this.gasStationService.getMunicipalities().subscribe(municipalities => this.userPreferencesService.setSpainMunicipalities(municipalities?.map(m => m.nombreMunicipio) || []));
    }

    updateUserPreferences(newPrefs: any): void {
        this.userPreferencesService.setUserPreferences(newPrefs);
    }

    updateThemeLanguage(newThemeLang: any): void {
        this.userPreferencesService.setThemeLanguage(newThemeLang);
    }
}