import { Component, signal, inject, effect, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GasStationService } from '../../../../services/gas-station/gas-station.service';
import { GasStation } from '../../../../Dto/gas-station';
import { MapPageComponent } from '../../map-page/map-page.component';

@Component({
  selector: 'app-user-preferences',
  imports: [CommonModule, FormsModule, MapPageComponent],
  templateUrl: './user-preferences.component.html',
  styleUrls: ['./user-preferences.component.css']
})
export class UserPreferencesComponent {
  private gasStationService = inject(GasStationService);

  fuelType = signal<string>('Todos');
  favoriteGasStation = signal<string>('');
  theme = signal<string>('Claro');
  language = signal<string>('Español');
  radioKm = signal<number>(5);
  precioMaximo = signal<number>(2.0);
  mapType = signal<string>('Mapa');
  avoidTolls = signal<boolean>(false);
  emissionLabel = signal<string>('Ninguna');

  fuelOptions = ['Todos', 'Gasolina', 'Diésel', 'Eléctrico'];
  themeOptions = ['Claro', 'Oscuro'];
  languageOptions = ['Español', 'Inglés'];
  gasStationOptions = ['Repsol', 'Cepsa', 'BP', 'Shell', 'Galp'];
  mapTypeOptions = ['Mapa', 'Mapa relieve', 'Satelite', 'Satelite etiquetas'];
  emissionLabelOptions = ['Ninguna', 'Eco', '0 Emisiones', 'B', 'C'];

  favoriteGasStations = signal<GasStation[]>([]);

  searchAddress = signal<string>('');
  searchResults = signal<GasStation[]>([]);
  selectedStation = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  hasSearched = signal<boolean>(false);

  showInfo = signal<boolean>(false);

  allStations = computed(() => 
    [...this.favoriteGasStations(), ...this.searchResults()].filter((s, i, arr) => 
      arr.findIndex(x => x.idEstacion === s.idEstacion) === i
    )
  );

  constructor() {
    effect(() => {
      this.searchAddress();
      this.hasSearched.set(false);
    });

    effect(() => {
      this.radioKm();
      this.hasSearched.set(false);
    });
  }

  searchGasStations(): void {
    this.searchResults.set([]);
    if (!this.searchAddress().trim()) return;

    this.isLoading.set(true);
    this.hasSearched.set(true);
    this.gasStationService.searchGasStations(this.searchAddress(), this.radioKm()).subscribe({
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

  toggleSelection(station: GasStation): void {
    const key = `${station.nombreEstacion} - ${station.direccion}`;
    if (this.selectedStation() === key) {
      this.selectedStation.set(null);
    } else {
      this.selectedStation.set(key);
    }
  }

  addSelectedGasStations(): void {
    if (this.selectedStation()) {
      const station = this.searchResults().find(s => `${s.nombreEstacion} - ${s.direccion}` === this.selectedStation());
      if (station && !this.isFavorite(station)) {
        this.favoriteGasStations.update(stations => [...stations, station]);
      }
      this.selectedStation.set(null);
    }
  }

  isFavorite(station: GasStation): boolean {
    return this.favoriteGasStations().some(f => f.idEstacion === station.idEstacion);
  }

  savePreferences(): void {

  }

  resetPreferences(): void {
    this.fuelType.set('Todos');
    this.favoriteGasStation.set('');
    this.theme.set('Claro');
    this.language.set('Español');
    this.radioKm.set(5);
    this.precioMaximo.set(2.0);
    this.mapType.set('Mapa');
    this.avoidTolls.set(false);
    this.emissionLabel.set('Ninguna');
    this.favoriteGasStations.set([]);
  }

  removeFavoriteGasStation(station: GasStation): void {
    this.favoriteGasStations.update(stations => stations.filter(s => s.idEstacion !== station.idEstacion));
  }

  toggleInfo(): void {
    this.showInfo.update(v => !v);
  }

  setSelectedStation(station: GasStation): void {
    this.selectedStation.set(`${station.nombreEstacion} - ${station.direccion}`);
  }
}