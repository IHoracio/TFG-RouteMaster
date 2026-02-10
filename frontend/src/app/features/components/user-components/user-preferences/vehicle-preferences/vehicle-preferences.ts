import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserPreferencesService } from '../../../../../services/user-page/user-preferences.service';
import { TranslationService } from '../../../../../services/translation.service';

@Component({
  selector: 'app-vehicle-preferences',
  imports: [CommonModule, FormsModule],
  templateUrl: './vehicle-preferences.html',
  styleUrls: ['./vehicle-preferences.css']
})
export class VehiclePreferencesComponent {
  userPreferencesService = inject(UserPreferencesService);
  translation = inject(TranslationService);

  setFuelType(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.userPreferencesService.setUserPreferences({ ...this.userPreferencesService.getUserPreferencesSignal()(), fuelType: value });
  }

  setMaxPrice(event: Event): void {
    const value = parseFloat((event.target as HTMLInputElement).value) || 0;
    this.userPreferencesService.setUserPreferences({ ...this.userPreferencesService.getUserPreferencesSignal()(), maxPrice: value });
  }

  setAvoidTolls(event: Event): void {
    const value = (event.target as HTMLInputElement).checked;
    this.userPreferencesService.setUserPreferences({ ...this.userPreferencesService.getUserPreferencesSignal()(), avoidTolls: value });
  }

  setEmissionType(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.userPreferencesService.setUserPreferences({ ...this.userPreferencesService.getUserPreferencesSignal()(), emissionType: value });
  }
}