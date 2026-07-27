import { Component, computed, inject, signal } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { UserPreferencesService } from '../../../../../services/user-page/user-preferences.service';
import { TranslationService } from '../../../../../services/singleton/translation.service';

@Component({
  selector: 'app-vehicle-preferences',
  imports: [FormsModule],
  templateUrl: './vehicle-preferences.html',
  styleUrls: ['./vehicle-preferences.css']
})
export class VehiclePreferencesComponent {
  private userPreferencesService = inject(UserPreferencesService);
  translation = inject(TranslationService);

  // Referencias directas a las señales
  userPreferences = this.userPreferencesService.userPreferences;
  fuelOptions = this.userPreferencesService.fuelOptions;

  // Busqueda de marca y logica
  brandSearch = signal<string>('');

  // Computed: Lógica reactiva de marcas
  showBrandDropdown = computed(() => this.brandSearch().length > 0);

  filteredBrands = computed(() => {
    const search = this.brandSearch().toLowerCase();
    const allBrands = this.userPreferencesService.gasStationBrandsOptions();
    return allBrands.filter(b => b.toLowerCase().includes(search)).slice(0, 4);
  });

  setFuelType(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.updateVehiclePref({ fuelType: value });
  }

  setMaxPrice(event: Event): void {
    const value = parseFloat((event.target as HTMLInputElement).value) || 0;
    this.updateVehiclePref({ maxPrice: value });
  }

  setRadioKm(event: Event): void {
    const value = parseInt((event.target as HTMLInputElement).value, 10);
    this.updateVehiclePref({ radioKm: value });

  }

  setAvoidTolls(event: Event): void {
    const value = (event.target as HTMLInputElement).checked;
    this.updateVehiclePref({ avoidTolls: value });
  }

  // --- Métodos de Marcas ---
  setBrandSearch(event: Event): void {
    this.brandSearch.set((event.target as HTMLInputElement).value);
  }

  addPreferredBrand(brand: string): void {
    const currentPrefs = this.userPreferences();
    if (!currentPrefs.preferredBrands.includes(brand)) {
      this.userPreferencesService.updateData(this.userPreferences, 'userPreferences', {
        ...currentPrefs,
        preferredBrands: [...currentPrefs.preferredBrands, brand.toUpperCase()]
      });
    }
    this.brandSearch.set('');
  }

  removePreferredBrand(brand: string): void {
    const currentPrefs = this.userPreferences();
    this.userPreferencesService.updateData(this.userPreferences, 'userPreferences', {
      ...currentPrefs,
      preferredBrands: currentPrefs.preferredBrands.filter((b: string) => b !== brand)
    });
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

  capitalize(str: string): string {
    return str ? str.charAt(0).toUpperCase() + str.slice(1).toLowerCase() : '';
  }

}