import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserPreferencesService } from '../../../../services/user-page/user-preferences.service';
import { forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { GenericPreferencesComponent } from './generic-preferences/generic-preferences';
import { GasStationsPreferencesComponent } from './gas-stations-preferences/gas-stations-preferences';
import { VehiclePreferencesComponent } from './vehicle-preferences/vehicle-preferences';
import { TranslationService } from '../../../../services/singleton/translation.service';

@Component({
  selector: 'app-user-preferences',
  imports: [CommonModule, FormsModule, GenericPreferencesComponent, GasStationsPreferencesComponent, VehiclePreferencesComponent],
  templateUrl: './user-preferences.component.html',
  styleUrls: ['./user-preferences.component.css']
})
export class UserPreferencesComponent {
  translation = inject(TranslationService);
  private userPreferencesService = inject(UserPreferencesService);
  router = inject(Router);

  userPreferences = this.userPreferencesService.userPreferences;
  favoriteGasStations = this.userPreferencesService.favoriteGasStations;
  themeLanguage = this.userPreferencesService.themeLanguage;

  activeSection = signal<string | null>(null);

  // Selectores de sección
  toggleSection(section: string): void {
    this.activeSection.update(v => v === section ? null : section);
  }

  savePreferences(): void {
    const prefs = this.userPreferences();
    const themeLang = this.themeLanguage();

    // 1. Llamadas al servidor
    const updatePrefs$ = this.userPreferencesService.updateUserPreferences(
      prefs.radioKm, prefs.fuelType, prefs.maxPrice, prefs.mapView, prefs.avoidTolls, prefs.preferredBrands
    );
    const updateTheme$ = this.userPreferencesService.updateUserThemeLanguage(themeLang.theme, themeLang.language);

    // 2. Ejecutar y sincronizar
    forkJoin([updatePrefs$, updateTheme$]).subscribe({
      next: () => {
        // Al usar Signals y estar vinculados al localStorage en el servicio, 
        // solo con que la petición sea exitosa ya estamos tranquilos.
        // Si el usuario vuelve atrás, los signals ya tienen los valores actuales.
        window.scrollTo(0, 0);
        this.router.navigate(['/user']);
      },
      error: (err) => alert('Error al guardar: ' + (err?.message || 'Desconocido'))
    });
  }

  resetPreferences(): void {
    const defaults = this.userPreferencesService.defaultPreferences();

    if (!defaults) {
      console.warn('No hay valores por defecto cargados');
      return;
    }

    // 1. Aplicamos los valores de la Signal de defaults a la Signal de usuario
    // Esto actualiza la UI y el LocalStorage inmediatamente
    this.userPreferencesService.updateData(this.userPreferences, 'userPreferences', {
      fuelType: defaults.fuelType,
      maxPrice: defaults.maxPrice,
      mapView: defaults.mapView,
      avoidTolls: defaults.avoidTolls,
      radioKm: defaults.radioKm,
      preferredBrands: defaults.preferredBrands || []
    });

    // 2. Persistimos en el servidor
    this.savePreferences();
  }

  // Helpers para el HTML
  isSectionActive(section: string): boolean {
    return this.activeSection() === section;
  }
}