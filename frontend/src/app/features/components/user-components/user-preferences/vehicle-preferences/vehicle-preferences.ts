import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserPreferencesService } from '../../../../../services/user-page/user-preferences.service';
import { TranslationService } from '../../../../../services/singleton/translation.service';

@Component({
  selector: 'app-vehicle-preferences',
  imports: [CommonModule, FormsModule],
  templateUrl: './vehicle-preferences.html',
  styleUrls: ['./vehicle-preferences.css']
})
export class VehiclePreferencesComponent {
  private userPreferencesService = inject(UserPreferencesService);
  translation = inject(TranslationService);

  // Referencias directas a las señales
  userPreferences = this.userPreferencesService.userPreferences;
  fuelOptions = this.userPreferencesService.fuelOptions;

  setFuelType(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.userPreferencesService.updateData(this.userPreferences, 'userPreferences', {
      ...this.userPreferences(),
      fuelType: value
    });
  }

  setMaxPrice(event: Event): void {
    const value = parseFloat((event.target as HTMLInputElement).value) || 0;
    this.updateVehiclePref({ maxPrice: value });
  }

  setAvoidTolls(event: Event): void {
    const value = (event.target as HTMLInputElement).checked;
    this.updateVehiclePref({ avoidTolls: value });
  }

  /**
   * Helper privado para evitar repetir la lógica de updateData
   */
  private updateVehiclePref(partial: Partial<any>): void {
    this.userPreferencesService.updateData(this.userPreferences, 'userPreferences', {
      ...this.userPreferences(),
      ...partial
    });
  }

}