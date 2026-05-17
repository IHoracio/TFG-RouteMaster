import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserPreferencesService } from '../../../../services/user-page/user-preferences.service';
import { catchError, EMPTY, exhaustMap, filter, forkJoin, Observable, Subject, tap } from 'rxjs';
import { Router } from '@angular/router';
import { GenericPreferencesComponent } from './generic-preferences/generic-preferences';
import { GasStationsPreferencesComponent } from './gas-stations-preferences/gas-stations-preferences';
import { VehiclePreferencesComponent } from './vehicle-preferences/vehicle-preferences';
import { TranslationService } from '../../../../services/singleton/translation.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ThemeService } from '../../../../services/singleton/theme.service';

@Component({
  selector: 'app-user-preferences',
  imports: [CommonModule, FormsModule, GenericPreferencesComponent, GasStationsPreferencesComponent, VehiclePreferencesComponent],
  templateUrl: './user-preferences.component.html',
  styleUrls: ['./user-preferences.component.css']
})
export class UserPreferencesComponent {
  translation = inject(TranslationService);
  theme = inject(ThemeService);
  private userPreferencesService = inject(UserPreferencesService);
  router = inject(Router);

  userPreferences = this.userPreferencesService.userPreferences;
  favoriteGasStations = this.userPreferencesService.favoriteGasStations;
  themeLanguage = this.userPreferencesService.themeLanguage;

  activeSection = signal<string | null>(null);

  // 1. Definimos el disparador
  private readonly saveTrigger = new Subject<void>();

  constructor() {
    // Solo inicializamos el "escuchador"
    this.initSaveSubscription();
  }

  /**
   * Configura la tubería (pipeline) de guardado.
   * Se separa para no ensuciar el constructor.
   */
  private initSaveSubscription(): void {
    this.saveTrigger.pipe(
      filter(() => this.userPreferencesService.hasChanges()), // Solo si hay cambios
      exhaustMap(() => this.savePreferencesInServer()),   // Ejecuta el proceso
      takeUntilDestroyed()                             // Autolimpiable
    ).subscribe();
  }

  // Selectores de sección
  toggleSection(section: string): void {
    this.activeSection.update(v => v === section ? null : section);
  }

  private savePreferencesInServer(): Observable<any> {
    const prefs = this.userPreferences();
    const themeLang = this.themeLanguage();

    // 1. Llamadas al servidor
    const updatePrefs$ = this.userPreferencesService.updateUserPreferences(
      prefs.radioKm, prefs.fuelType, prefs.maxPrice, prefs.mapView, prefs.avoidTolls, prefs.preferredBrands
    );
    const updateTheme$ = this.userPreferencesService.updateUserThemeLanguage(themeLang.theme, themeLang.language);

    // 2. Ejecutar y sincronizar
    return forkJoin([updatePrefs$, updateTheme$]).pipe(
      tap(() => {
        // Sincronizamos las señales de "servidor" con las actuales tras el éxito
        this.userPreferencesService.serverUserPreferences.set({ ...this.userPreferences() });
        this.userPreferencesService.serverThemeLanguage.set({ ...this.themeLanguage() });

        this.translation.setLanguage(themeLang.language);
        this.theme.setTheme(themeLang.theme);

        window.scrollTo(0, 0);
        this.router.navigate(['/user']);
      }),
      catchError((err) => {
        alert('Error al guardar: ' + (err?.message || 'Desconocido'));
        // Retornamos EMPTY para que el flujo no muera y el usuario pueda reintentar
        return EMPTY;
      })
    );
  }

  /**
   * Dispara el flujo de guardado
   */
  savePreferences(): void {
    this.saveTrigger.next();
  }

  resetPreferences(): void {
    const defaults = this.userPreferencesService.defaultPreferences();
    const prefs = this.userPreferences();

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
      preferredBrands: prefs.preferredBrands
    });

    this.userPreferencesService.updateData(this.themeLanguage, 'themeLanguage', {
      theme: 'LIGHT',
      language: 'ES'
    });

    // 2. Persistimos en el servidor
    this.saveTrigger.next();
  }

  // Helpers para el HTML
  isSectionActive(section: string): boolean {
    return this.activeSection() === section;
  }
}