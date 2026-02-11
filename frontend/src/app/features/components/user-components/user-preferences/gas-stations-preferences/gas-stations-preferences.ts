import { Component, computed, effect, inject, signal } from '@angular/core';
import { UserPreferencesService } from '../../../../../services/user-page/user-preferences.service';
import { GasStationService } from '../../../../../services/user-page/gas-station/gas-station.service';
import { GasStationSelectionService } from '../../../../../services/user-page/gas-station-selection/gas-station-selection.service';
import { TranslationService } from '../../../../../services/translation.service';
import { FavouriteGasStation, GasStation } from '../../../../../Dto/gas-station';
import { MapPageComponent } from '../../../../pages/map-page/map-page.component';

@Component({
  selector: 'app-gas-stations-preferences',
  imports: [MapPageComponent],
  templateUrl: './gas-stations-preferences.html',
  styleUrl: './gas-stations-preferences.css',
})
export class GasStationsPreferencesComponent {
  userPreferencesService = inject(UserPreferencesService);
  gasStationService = inject(GasStationService);
  gasStationSelectionService = inject(GasStationSelectionService);
  translation = inject(TranslationService);

  selectedGasStation = signal<GasStation | null>(null);
  favoriteGasStations = this.userPreferencesService.getFavoriteGasStationsSignal();
  searchAddress = signal<string>('');
  searchResults = signal<GasStation[]>([]);
  isLoading = signal<boolean>(false);
  hasSearched = signal<boolean>(false);
  brandSearch = signal<string>('');
  showBrandDropdown = signal<boolean>(false);
  alias = signal<string>('');

  filteredBrands = computed(() => {
    const search = this.brandSearch().toLowerCase();
    return this.userPreferencesService.getGasStationBrandsOptionsSignal()().filter(brand => brand.toLowerCase().includes(search)).slice(0, 4);
  });

  filteredMunicipalities = computed(() => {
    const search = this.searchAddress().toLowerCase();
    return this.userPreferencesService.getSpainMunicipalitiesSignal()().filter(municipality => municipality.toLowerCase().includes(search)).slice(0, 4);
  });

  allStations = computed(() => {
    const favorites = this.favoriteGasStations();
    const results = this.searchResults();
    return [...favorites, ...results].filter((s, i, arr) => arr.findIndex(x => x.idEstacion === s.idEstacion) === i).filter(s => s.latitud !== undefined && s.longitud !== undefined).slice(0, 50);
  });

  constructor() {
    effect(() => {
      this.brandSearch();
      this.showBrandDropdown.set(this.brandSearch().length > 0);
    });

    effect(() => {
      this.gasStationSelectionService.selectedStation.set(this.selectedGasStation());
    });
  }

  setBrandSearch(event: Event): void {
    this.brandSearch.set((event.target as HTMLInputElement).value);
  }

  addPreferredBrand(brand: string): void {
    const prefs = this.userPreferencesService.getUserPreferencesSignal()();
    if (!prefs.preferredBrands.includes(brand)) {
      this.userPreferencesService.setUserPreferences({ ...prefs, preferredBrands: [...prefs.preferredBrands, brand] });
    }
    this.brandSearch.set('');
  }

  removePreferredBrand(brand: string): void {
    const prefs = this.userPreferencesService.getUserPreferencesSignal()();
    this.userPreferencesService.setUserPreferences({ ...prefs, preferredBrands: prefs.preferredBrands.filter((b: string) => b !== brand) });
  }

  setRadioKm(event: Event): void {
    const value = parseInt((event.target as HTMLInputElement).value, 10);
    this.userPreferencesService.setUserPreferences({ ...this.userPreferencesService.getUserPreferencesSignal()(), radioKm: value });
  }

  setSearchAddress(event: Event): void {
    this.searchAddress.set((event.target as HTMLInputElement).value);
  }

  setMunicipality(municipality: string): void {
    this.searchAddress.set(municipality);
    this.searchGasStations();
  }

  searchGasStations(): void {
    this.searchResults.set([]);
    if (!this.searchAddress().trim()) return;
    this.isLoading.set(true);
    this.hasSearched.set(true);
    const normalized = this.normalize(this.searchAddress());
    this.gasStationService.getGasStationFromDirectionInRadius(normalized, this.userPreferencesService.getUserPreferencesSignal()().radioKm || 0).subscribe({
      next: (results) => {
        this.searchResults.set(results || []);
        this.selectedGasStation.set(null);
        this.isLoading.set(false);
      },
      error: () => {
        alert('Error al buscar gasolineras.');
        this.isLoading.set(false);
      }
    });
  }

  toggleSelection(station: GasStation): void {
    this.selectedGasStation.set(this.selectedGasStation() === station ? null : station);
  }

  setSelectedStation(station: GasStation | FavouriteGasStation): void {
    this.selectedGasStation.set(station);
  }

  setAlias(event: Event): void {
    this.alias.set((event.target as HTMLInputElement).value);
  }

  addSelectedGasStations(): void {
    if (this.selectedGasStation() && this.alias().trim()) {
      const station = this.selectedGasStation();
      if (station && !this.isFavorite(station)) {
        const alias = this.alias();
        if (this.favoriteGasStations().some(f => f.alias === alias)) {
          alert('Alias ya existe.');
          return;
        }
        const favorite: FavouriteGasStation = { ...station, alias };
        this.favoriteGasStations.update(stations => [...stations, favorite]);
        this.alias.set('');
        this.selectedGasStation.set(null);
      }
    }
  }

  isFavorite(station: GasStation): boolean {
    return this.favoriteGasStations().some(f => f.idEstacion === station.idEstacion);
  }

  removeFavoriteGasStation(station: { idEstacion: number }): void {
    this.favoriteGasStations.update(stations => stations.filter(s => s.idEstacion !== station.idEstacion));
  }

  renameFavoriteGasStation(station: FavouriteGasStation): void {
    const newAlias = prompt('Nuevo alias:', station.alias);
    if (newAlias && newAlias.trim() !== station.alias) {
      if (this.favoriteGasStations().some(f => f.alias === newAlias.trim())) {
        alert('Alias ya existe.');
        return;
      }
      this.favoriteGasStations.update(stations => stations.map(s => s.alias === station.alias ? { ...s, alias: newAlias.trim() } : s));
    }
  }

  normalize(text: string): string {
    return text.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  }

  capitalize(str: string): string {
    return str ? str.charAt(0).toUpperCase() + str.slice(1).toLowerCase() : '';
  }
}