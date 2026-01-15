import { Component, signal, inject, effect, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GasStationService } from '../../../../services/user-page/gas-station/gas-station.service';
import { GasStation, GasStationFavourite } from '../../../../Dto/gas-station';
import { MapPageComponent } from '../../map-page/map-page.component';
import { UserPreferencesService } from '../../../../services/user-page/user-preferences.service';

@Component({
  selector: 'app-user-preferences',
  imports: [CommonModule, FormsModule, MapPageComponent],
  templateUrl: './user-preferences.component.html',
  styleUrl: './user-preferences.component.css'
})
export class UserPreferencesComponent implements OnInit {
  private gasStationService = inject(GasStationService);
  private userPreferencesService = inject(UserPreferencesService);

  fuelType = signal<string>('');
  theme = signal<string>('');
  language = signal<string>('');
  radioKm = signal<number>(0);
  maxPrice = signal<number>(0);
  mapType = signal<string>('');
  avoidTolls = signal<boolean>(false);
  emissionType = signal<string>('');
  preferredBrands = signal<string[]>([]);

  fuelOptions = signal<string[]>([]);
  themeOptions = signal<string[]>([]);
  languageOptions = signal<string[]>([]);
  gasStationBrandsOptions = signal<string[]>([]);
  spainMunicipalities = signal<string[]>([]);
  mapTypeOptions = signal<string[]>([]);
  emissionLabelOptions = signal<string[]>([]);

  favoriteGasStations = signal<GasStationFavourite[]>([]);
  deletedFavourites = signal<GasStationFavourite[]>([]);

  searchAddress = signal<string>('');
  searchResults = signal<GasStation[]>([]);
  selectedStation = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  hasSearched = signal<boolean>(false);

  showRadiusInfo = signal<boolean>(false);
  showGasInfo = signal<boolean>(false);

  isLoadingPreferences = signal<boolean>(true);

  brandSearch = signal<string>('');
  showBrandDropdown = signal<boolean>(false);
  showAddressDropdown = signal<boolean>(false);

  alias = signal<string>('');

  private userId: string = "1";
  private email: string = 'prueba@gmail.com';

  filteredBrands = computed(() => {
    const search = this.brandSearch().toLowerCase();
    return this.gasStationBrandsOptions().filter(brand => brand.toLowerCase().includes(search)).slice(0, 4);
  });

  filteredMunicipalities = computed(() => {
    const search = this.searchAddress().toLowerCase();
    return this.spainMunicipalities().filter(municipalities => municipalities.toLowerCase().includes(search)).slice(0, 4);
  });

  allStations = computed(() =>
    [...this.favoriteGasStations(), ...this.searchResults()].filter((s, i, arr) =>
      arr.findIndex(x => x.idEstacion === s.idEstacion) === i
    )
  );

  ngOnInit(): void {
    this.loadOptions();
    this.userPreferencesService.getUserPreferences(this.userId, this.email).subscribe({
      next: (data) => {
        this.preferredBrands.set(data.preferredBrands || []);
        this.avoidTolls.set(data.avoidTolls || false);
        this.radioKm.set(data.radioKm || 0);
        this.fuelType.set(data.fuelType || '');
        this.emissionType.set(data.emissionType || '');
        this.maxPrice.set(data.maxPrice || 0);
        this.mapType.set(data.mapView || '');
        this.theme.set(data.theme || '');
        this.language.set(data.language || '');
        this.isLoadingPreferences.set(false);
      },
      error: (error) => {
        this.isLoadingPreferences.set(false);
      }
    });

    this.userPreferencesService.getUserGasStationFavourites(this.email).subscribe({
      next: (data) => {
        this.favoriteGasStations.set(data || []);
        this.favoriteGasStations().forEach(favorite => {
          if (!favorite.latitud || !favorite.longitud) {
            this.userPreferencesService.getGasStation(favorite.idEstacion).subscribe({
              next: (fullStation) => {
                this.favoriteGasStations.update(stations =>
                  stations.map(s => s.idEstacion === favorite.idEstacion ? { ...fullStation, alias: favorite.alias } : s)
                );
              },
              error: (err) => {
                console.error('Error obteniendo datos completos de gasolinera favorita:', err);
              }
            });
          }
        });
        this.isLoadingPreferences.set(false);
      },
      error: (error) => {
        this.isLoadingPreferences.set(false);
      }
    });
  }

  loadOptions(): void {
    this.userPreferencesService.getFuelTypes().subscribe(options => this.fuelOptions.set(options?.map(p => p.code) || []));
    this.userPreferencesService.getThemes().subscribe(options => this.themeOptions.set(options?.map(p => p.code) || []));
    this.userPreferencesService.getLanguages().subscribe(options => this.languageOptions.set(options?.map(p => p.code) || []));
    this.userPreferencesService.getGasStationBrands().subscribe(options => this.gasStationBrandsOptions.set(options || []));
    this.userPreferencesService.getMapTypes().subscribe(options => this.mapTypeOptions.set(options?.map(p => p.code) || []));
    this.userPreferencesService.getEmissionLabels().subscribe(options => this.emissionLabelOptions.set(options?.map(p => p.code) || []));
    this.userPreferencesService.getMunicipalities().subscribe(municipalities => this.spainMunicipalities.set(municipalities?.map(m => m.nombreMunicipio) || []));
  }

  constructor() {
    effect(() => {
      this.searchAddress();
      this.hasSearched.set(false);
    });

    effect(() => {
      this.radioKm();
      this.hasSearched.set(false);
    });

    effect(() => {
      this.brandSearch();
      this.showBrandDropdown.set(this.brandSearch().length > 0);
    });

    effect(() => {
      this.searchAddress();
      this.showAddressDropdown.set(this.searchAddress().length > 0);
    });

    effect(() => {
      if (this.fuelType() === 'ELECTRIC') {
        this.maxPrice.set(5);
      }
    });
  }

  setFuelType(event: Event): void {
    this.fuelType.set((event.target as HTMLSelectElement).value);
  }

  setMaxPrice(event: Event): void {
    this.maxPrice.set(parseFloat((event.target as HTMLInputElement).value) || 0);
  }

  setMapType(event: Event): void {
    this.mapType.set((event.target as HTMLSelectElement).value);
  }

  setAvoidTolls(event: Event): void {
    this.avoidTolls.set((event.target as HTMLInputElement).checked);
  }

  setEmissionLabel(event: Event): void {
    this.emissionType.set((event.target as HTMLSelectElement).value);
  }

  setTheme(event: Event): void {
    this.theme.set((event.target as HTMLSelectElement).value);
  }

  setLanguage(event: Event): void {
    this.language.set((event.target as HTMLSelectElement).value);
  }

  setRadioKm(event: Event): void {
    this.radioKm.set(parseInt((event.target as HTMLInputElement).value, 10));
  }

  setSearchAddress(event: Event): void {
    this.searchAddress.set((event.target as HTMLInputElement).value);
  }

  setBrandSearch(event: Event): void {
    this.brandSearch.set((event.target as HTMLInputElement).value);
  }

  setAlias(event: Event): void {
    this.alias.set((event.target as HTMLInputElement).value);
  }

  setSelectedStation(station: GasStation | GasStationFavourite): void {
    this.selectedStation.set(`${station.nombreEstacion} - ${station.direccion}`);
  }

  setMunicipality(municipality: string): void {
    this.searchAddress.set(municipality);
    this.showAddressDropdown.set(false);
    this.searchGasStations();
  }

  searchGasStations(): void {
    this.searchResults.set([]);
    if (!this.searchAddress().trim()) return;

    this.isLoading.set(true);
    this.hasSearched.set(true);
    const normalizedAddress = this.normalize(this.searchAddress());
    this.gasStationService.searchGasStations(normalizedAddress, this.radioKm()).subscribe({
      next: (results) => {
        this.searchResults.set(results || []);
        this.selectedStation.set(null);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error fetching gas stations:', error);
        alert('Error al buscar gasolineras. Intenta de nuevo.');
        this.isLoading.set(false);
      }
    });
  }

  addSelectedGasStations(): void {
    if (this.selectedStation() && this.alias().trim()) {
      const station = this.searchResults().find(s => `${s.nombreEstacion} - ${s.direccion}` === this.selectedStation());
      if (station && !this.isFavorite(station)) {
        if (this.favoriteGasStations().some(f => f.alias === this.alias())) {
          alert('El alias ya existe. Elige uno diferente.');
          return;
        }
        const favorite: GasStationFavourite = { ...station, alias: this.alias() };
        this.favoriteGasStations.update(stations => [...stations, favorite]);
        this.alias.set('');
        this.selectedStation.set(null);
      }
    }
  }

  isFavorite(station: GasStation): boolean {
    return this.favoriteGasStations().some(f => f.idEstacion === station.idEstacion);
  }

  toggleInfo(): void {
    this.showRadiusInfo.update(v => !v);
  }

  toggleSelection(station: GasStation): void {
    const key = `${station.nombreEstacion} - ${station.direccion}`;
    if (this.selectedStation() === key) {
      this.selectedStation.set(null);
    } else {
      this.selectedStation.set(key);
    }
  }

  toggleGasInfo(): void {
    this.showGasInfo.update(v => !v);
  }

  savePreferences(): void {
    if (!this.userId || !this.email) return;
    let mapView = this.mapType();

    this.userPreferencesService.updateUserPreferences(this.userId, this.email, this.radioKm(), this.fuelType(), this.emissionType(), this.maxPrice(), mapView, this.preferredBrands(), this.theme(), this.language()).subscribe({
      next: () => {
        console.log('Preferencias guardadas exitosamente.');
        window.location.reload();
      },
      error: (error) => {
        console.error('Error al guardar preferencias. Intenta de nuevo.');
      }
    });

    this.favoriteGasStations().forEach(favorite => {
      this.userPreferencesService.updateGasStationFavourites(this.email, favorite.alias, favorite.idEstacion).subscribe({
        next: () => {
          console.log(`Gasolinera favorita ${favorite.alias} guardada.`);
        },
        error: (error) => {
          console.error(`Error al guardar gasolinera favorita ${favorite.alias}:`, error);
        }
      });
    });

    this.deletedFavourites().forEach(favorite => {
      this.userPreferencesService.deleteGasStationFavourites(this.email, favorite.alias, favorite.idEstacion).subscribe({
        next: () => {
          console.log(`Gasolinera favorita ${favorite.alias} eliminada.`);
        },
        error: (error) => {
          console.error(`Error al eliminar gasolinera favorita ${favorite.alias}:`, error);
        }
      });
    });

    this.deletedFavourites.set([]);
  }

  resetPreferences(): void {
    this.fuelType.set('ALL');
    this.theme.set('LIGHT');
    this.language.set('ES');
    this.radioKm.set(1);
    this.maxPrice.set(2);
    this.mapType.set('MAP');
    this.avoidTolls.set(false);
    this.emissionType.set('');
    this.preferredBrands.set([]);
    this.favoriteGasStations.set([]);
    this.deletedFavourites.set([]);

    this.userPreferencesService.updateUserPreferences(this.userId, this.email, this.radioKm(), this.fuelType(), this.emissionType(), this.maxPrice(), this.mapType(), this.preferredBrands(), this.theme(), this.language()).subscribe({
      next: () => {
        console.log('Preferencias reiniciadas a por defecto.');
        window.location.reload();
      },
      error: (error) => {
        console.error('Error al reiniciar preferencias.');
      }
    });
  }

  removeFavoriteGasStation(station: { idEstacion: number }): void {
    const favorite = this.favoriteGasStations().find(f => f.idEstacion === station.idEstacion);
    if (favorite) {
      this.favoriteGasStations.update(stations => stations.filter(s => s.idEstacion !== station.idEstacion));
      this.deletedFavourites.update(deleted => [...deleted, favorite]);
    }
  }

  addPreferredBrand(brand: string): void {
    if (!this.preferredBrands().includes(brand)) {
      this.preferredBrands.update(brands => [...brands, brand]);
    }
    this.brandSearch.set('');
  }

  removePreferredBrand(brand: string): void {
    this.preferredBrands.update(brands => brands.filter(b => b !== brand));
  }

  normalize(text: string): string {
    return text.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  }

  capitalize(str: string): string {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
  }
}