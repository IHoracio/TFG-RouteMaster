import { Component, computed, effect, inject, signal } from '@angular/core';
import { UserPreferencesService } from '../../../../../services/user-page/user-preferences.service';
import { GasStationService } from '../../../../../services/user-page/gas-station/gas-station.service';
import { GasStationSelectionService } from '../../../../../services/user-page/gas-station-selection/gas-station-selection.service';
import { FavouriteGasStation, GasStation } from '../../../../../Dto/gas-station';
import { MapPageComponent } from '../../../../pages/map-page/map-page.component';
import { GoogleAutocompleteComponent } from '../../../google-autocomplete/google-autocomplete.component';
import { PlaceSelection } from '../../../../../Dto/place-selection';
import { TranslationService } from '../../../../../services/singleton/translation.service';

@Component({
  selector: 'app-gas-stations-preferences',
  imports: [MapPageComponent, GoogleAutocompleteComponent],
  templateUrl: './gas-stations-preferences.html',
  styleUrl: './gas-stations-preferences.css',
})
export class GasStationsPreferencesComponent {
  private userPreferencesService = inject(UserPreferencesService);
  private gasStationService = inject(GasStationService);
  private gasStationSelectionService = inject(GasStationSelectionService);
  translation = inject(TranslationService);

  // Signals de Estado Local (Volátiles)
  searchResults = signal<GasStation[]>([]);
  isLoading = signal<boolean>(false);
  hasSearched = signal<boolean>(false);
  brandSearch = signal<string>('');
  alias = signal<string>('');
  selectedPlaceForGas = signal<PlaceSelection | null>(null);

  // Acceso a Signals Globales (Lectura directa)
  favoriteGasStations = this.userPreferencesService.favoriteGasStations;
  userPreferences = this.userPreferencesService.userPreferences;

  // Signal para la estación seleccionada (comunicación con el mapa)
  // Usamos un getter/setter para que cuando cambie, notifique al SelectionService
  private _selectedGasStation = signal<GasStation | null>(null);
  get selectedGasStation() { return this._selectedGasStation(); }
  set selectedGasStation(val: GasStation | null) {
    this._selectedGasStation.set(val);
    this.gasStationSelectionService.selectedStation.set(val);
  }

  // Computed: Lógica reactiva de marcas
  showBrandDropdown = computed(() => this.brandSearch().length > 0);

  filteredBrands = computed(() => {
    const search = this.brandSearch().toLowerCase();
    const allBrands = this.userPreferencesService.gasStationBrandsOptions();
    return allBrands.filter(b => b.toLowerCase().includes(search)).slice(0, 4);
  });

  // Computed: Combinación de favoritos y resultados para el mapa
  allStations = computed(() => {
    const favorites = this.favoriteGasStations();
    const results = this.searchResults();
    // Unificar por ID para evitar duplicados en el mapa
    const combined = [...favorites, ...results];
    return combined
      .filter((s, i, arr) => arr.findIndex(x => x.idEstacion === s.idEstacion) === i)
      .filter(s => s.latitud !== undefined && s.longitud !== undefined)
      .slice(0, 50);
  });

  // --- Métodos de Marcas ---
  setBrandSearch(event: Event): void {
    this.brandSearch.set((event.target as HTMLInputElement).value);
  }

  addPreferredBrand(brand: string): void {
    const currentPrefs = this.userPreferences();
    if (!currentPrefs.preferredBrands.includes(brand)) {
      this.userPreferencesService.updateData(this.userPreferences, 'userPreferences', {
        ...currentPrefs,
        preferredBrands: [...currentPrefs.preferredBrands, brand]
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

  // --- Métodos de Búsqueda ---
  setRadioKm(event: Event): void {
    const value = parseInt((event.target as HTMLInputElement).value, 10);
    this.userPreferencesService.updateData(this.userPreferences, 'userPreferences', {
      ...this.userPreferences(),
      radioKm: value
    });
  }

  searchGasStations(): void {
    const selection = this.selectedPlaceForGas();
    if (!selection) return;

    this.isLoading.set(true);
    this.hasSearched.set(true);

    const searchIdentifier = `place_id:${selection.placeId}`;
    const radius = this.userPreferences().radioKm || 0;

    this.gasStationService.getGasStationFromDirectionInRadius(searchIdentifier, radius).subscribe({
      next: (results) => {
        this.searchResults.set(results || []);
        this.selectedGasStation = null;
        this.isLoading.set(false);
      },
      error: () => {
        alert('Error al buscar gasolineras.');
        this.isLoading.set(false);
      }
    });
  }

  handleGasStationPlaceSelected(selection: PlaceSelection) {
    this.selectedPlaceForGas.set(selection);
  }

  // --- Gestión de Favoritos ---
  addSelectedGasStations(): void {
    const station = this.selectedGasStation;
    const aliasVal = this.alias().trim();

    if (station && aliasVal) {
      if (this.favoriteGasStations().some(f => f.alias === aliasVal || f.idEstacion === station.idEstacion)) {
        alert('La gasolinera o el alias ya están en tus favoritos.');
        return;
      }
      const favorite: FavouriteGasStation = { ...station, alias: aliasVal };
      this.userPreferencesService.updateData(
        this.favoriteGasStations,
        'favoriteGasStations',
        [...this.favoriteGasStations(), favorite]
      );
      this.alias.set('');
      this.selectedGasStation = null;
    }
  }

  setAlias(event: Event): void {
    this.alias.set((event.target as HTMLInputElement).value);
  }

  removeFavoriteGasStation(stationId: number): void {
    this.userPreferencesService.updateData(
      this.favoriteGasStations,
      'favoriteGasStations',
      this.favoriteGasStations().filter(s => s.idEstacion !== stationId)
    );
  }

  renameFavoriteGasStation(station: FavouriteGasStation): void {
    const newAlias = prompt('Nuevo alias:', station.alias);
    if (newAlias && newAlias.trim() !== station.alias) {
      const aliasTrimmed = newAlias.trim();
      if (this.favoriteGasStations().some(f => f.alias === aliasTrimmed)) {
        alert('Ese alias ya existe.');
        return;
      }
      this.userPreferencesService.updateData(
        this.favoriteGasStations,
        'favoriteGasStations',
        this.favoriteGasStations().map(s => s.idEstacion === station.idEstacion ? { ...s, alias: aliasTrimmed } : s)
      );
    }
  }

  isFavorite(stationId: number): boolean {
    return this.favoriteGasStations().some(f => f.idEstacion === stationId);
  }

  // Helpers UI
  capitalize(str: string): string {
    return str ? str.charAt(0).toUpperCase() + str.slice(1).toLowerCase() : '';
  }

}