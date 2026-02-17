import { Component, inject } from '@angular/core';
import { UserPreferencesService } from '../../../../../services/user-page/user-preferences.service';
import { TranslationService } from '../../../../../services/translation.service';
import { ThemeService } from '../../../../../services/theme.service';

@Component({
  selector: 'app-generic-preferences',
  imports: [],
  templateUrl: './generic-preferences.html',
  styleUrl: './generic-preferences.css',
})
export class GenericPreferencesComponent {
  userPreferencesService = inject(UserPreferencesService);
  translation = inject(TranslationService);
  themeService = inject(ThemeService);

  setMapType(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.userPreferencesService.setUserPreferences({ ...this.userPreferencesService.getUserPreferencesSignal()(), mapView: value });
  }

  setTheme(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.userPreferencesService.setThemeLanguage({ ...this.userPreferencesService.getThemeLanguageSignal()(), theme: value });
    this.themeService.setTheme(value);
  }

  setLanguage(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.userPreferencesService.setThemeLanguage({ ...this.userPreferencesService.getThemeLanguageSignal()(), language: value });
  }
}
