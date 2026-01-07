import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-preferences',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-preferences.component.html',
  styleUrls: ['./user-preferences.component.css']
})
export class UserPreferencesComponent {
  fuelType = signal<string>('Todos');
  favoriteGasStation = signal<string>('');
  theme = signal<string>('Claro');
  language = signal<string>('Español');
  radioKm = signal<number>(5);
  precioMaximo = signal<number>(2.0);

  fuelOptions = ['Todos', 'Gasolina', 'Diésel', 'Eléctrico'];
  themeOptions = ['Claro', 'Oscuro'];
  languageOptions = ['Español', 'Inglés'];
  gasStationOptions = ['Repsol', 'Cepsa', 'BP', 'Shell', 'Galp'];

  favoriteGasStations = signal<string[]>([]);

  newGasStation = signal<string>('');

  savePreferences(): void {

  }

  resetPreferences(): void {
    this.fuelType.set('Todos');
    this.favoriteGasStation.set('');
    this.theme.set('Claro');
    this.language.set('Español');
    this.radioKm.set(5);
    this.precioMaximo.set(2.0);
    this.favoriteGasStations.set([]);
  }

  addFavoriteGasStation(): void {
    if (this.newGasStation().trim() && !this.favoriteGasStations().includes(this.newGasStation().trim())) {
      this.favoriteGasStations.update(stations => [...stations, this.newGasStation().trim()]);
      this.newGasStation.set('');
    }
  }

  removeFavoriteGasStation(station: string): void {
    this.favoriteGasStations.update(stations => stations.filter(s => s !== station));
  }
}