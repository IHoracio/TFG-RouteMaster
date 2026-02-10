import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserPreferencesService } from '../../../../services/user-page/user-preferences.service';
import { forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { TranslationService } from '../../../../services/translation.service';
import { UserDataService } from '../../../../services/singleton/user-data.service';
import { GenericPreferencesComponent } from './generic-preferences/generic-preferences';
import { GasStationsPreferencesComponent } from './gas-stations-preferences/gas-stations-preferences';
import { VehiclePreferencesComponent } from './vehicle-preferences/vehicle-preferences';

@Component({
  selector: 'app-user-preferences',
  imports: [CommonModule, FormsModule, GenericPreferencesComponent, GasStationsPreferencesComponent, VehiclePreferencesComponent],
  templateUrl: './user-preferences.component.html',
  styleUrls: ['./user-preferences.component.css']
})
export class UserPreferencesComponent {
  translation = inject(TranslationService);
  userPreferencesService = inject(UserPreferencesService);
  userDataService = inject(UserDataService);
  router = inject(Router);

  defaultUserPreferences = this.userPreferencesService.getDefaultPreferencesSignal();
  showGeneric = signal<boolean>(false);
  showVehicle = signal<boolean>(false);
  showGasStations = signal<boolean>(false);

  toggleGeneric(): void {
    this.showGeneric.update(v => !v);
  }

  toggleVehicle(): void {
    this.showVehicle.update(v => !v);
  }

  toggleGasStations(): void {
    this.showGasStations.update(v => !v);
  }

  savePreferences(): void {
    if (!this.defaultUserPreferences()) {
      alert('Cargando preferencias...');
      return;
    }

    const prefs = this.userPreferencesService.getUserPreferencesSignal()();
    const themeLang = this.userPreferencesService.getThemeLanguageSignal()();

    const updatePrefs$ = this.userPreferencesService.updateUserPreferences(
      prefs.radioKm, prefs.fuelType, prefs.emissionType, prefs.maxPrice, prefs.mapView, prefs.avoidTolls, prefs.preferredBrands
    );

    const updateTheme$ = this.userPreferencesService.updateUserThemeLanguage(themeLang.theme, themeLang.language);

    const newFavorites = this.userPreferencesService.getFavoriteGasStationsSignal()().filter(f => !this.userPreferencesService.getFavoriteGasStationsSignal()().some(orig => orig.idEstacion === f.idEstacion));
    const updateFavorites$ = newFavorites.map(f => this.userPreferencesService.updateFavouriteGasStations(f.alias, f.idEstacion));

    forkJoin([updatePrefs$, updateTheme$, ...updateFavorites$]).subscribe({
      next: () => {
        this.userDataService.updateUserPreferences(prefs);
        this.userDataService.updateThemeLanguage(themeLang);
        history.scrollRestoration = 'manual';
        window.scrollTo(0, 0);
        this.router.navigate([`/user`]);
      },
      error: (err) => {
        alert('Error saving: ' + (err?.message || 'Unknown'));
      }
    });
  }

  resetPreferences(): void {
    if (!this.defaultUserPreferences()) {
      alert('Cargando...');
      return;
    }

    const defaults = this.defaultUserPreferences();
    this.userPreferencesService.setUserPreferences({
      fuelType: defaults.fuelType || 'GASOLINE',
      emissionType: defaults.emissionType || 'B',
      maxPrice: defaults.maxPrice || 1.5,
      mapView: defaults.mapView || 'MAP',
      avoidTolls: defaults.avoidTolls || false,
      radioKm: defaults.radioKm || 1,
      preferredBrands: defaults.preferredBrands || []
    });
    this.userPreferencesService.setThemeLanguage({ theme: 'LIGHT', language: 'ES' });
    this.userPreferencesService.setFavoriteGasStations([]);

    const prefs = this.userPreferencesService.getUserPreferencesSignal()();
    const themeLang = this.userPreferencesService.getThemeLanguageSignal()();

    const updatePrefs$ = this.userPreferencesService.updateUserPreferences(
      prefs.radioKm, prefs.fuelType, prefs.emissionType, prefs.maxPrice, prefs.mapView, prefs.avoidTolls, prefs.preferredBrands
    );

    const updateTheme$ = this.userPreferencesService.updateUserThemeLanguage(themeLang.theme, themeLang.language);

    forkJoin([updatePrefs$, updateTheme$]).subscribe({
      next: () => {
        this.userDataService.updateUserPreferences(prefs);
        this.userDataService.updateThemeLanguage(themeLang);
        history.scrollRestoration = 'manual';
        window.scrollTo(0, 0);
        this.router.navigate(['/user-preferences']);
      },
      error: (err) => {
        alert('Error resetting: ' + (err?.message || 'Error desconocido'));
      }
    });
  }
}