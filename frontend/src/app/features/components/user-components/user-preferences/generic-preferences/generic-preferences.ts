import { Component, inject } from '@angular/core';
import { UserPreferencesService } from '../../../../../services/user-page/user-preferences.service';
import { ThemeService } from '../../../../../services/singleton/theme.service';
import { TranslationService } from '../../../../../services/singleton/translation.service';

@Component({
  selector: 'app-generic-preferences',
  imports: [],
  templateUrl: './generic-preferences.html',
  styleUrl: './generic-preferences.css',
})
export class GenericPreferencesComponent {
  private userPreferencesService = inject(UserPreferencesService);
  private themeService = inject(ThemeService);
  translation = inject(TranslationService);

  // Referencias directas a las señales para el HTML
  userPreferences = this.userPreferencesService.userPreferences;
  themeLanguage = this.userPreferencesService.themeLanguage;

  // Catálogos
  mapTypeOptions = this.userPreferencesService.mapOptions;
  themeOptions = this.userPreferencesService.themeOptions;
  languageOptions = this.userPreferencesService.languageOptions;

  setMapType(event: Event): void {
    const value = (event.target as HTMLSelectElement).value; // Extraemos el string
    this.userPreferencesService.updateData(this.userPreferences, 'userPreferences', {
      ...this.userPreferences(),
      mapView: value
    });
  }

  setTheme(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.userPreferencesService.updateData(this.themeLanguage, 'themeLanguage', {
      ...this.themeLanguage(),
      theme: value
    });
    this.themeService.setTheme(value);
  }

  setLanguage(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.userPreferencesService.updateData(this.themeLanguage, 'themeLanguage', {
      ...this.themeLanguage(),
      language: value
    });
    this.translation.setLanguage(value);
  }
}
