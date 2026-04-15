import { Component, signal, inject, computed, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserPreferencesService } from '../../../../services/user-page/user-preferences.service';
import { FavouriteGasStation } from '../../../../Dto/gas-station';
import { UserInfoService } from '../../../../services/user-page/user-info.service';
import { TranslationService } from '../../../../services/singleton/translation.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-user-info',
  imports: [CommonModule],
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent {

  translation = inject(TranslationService);

  private userInfoService = inject(UserInfoService);
  private userPreferencesService = inject(UserPreferencesService);

  // Signals Globales (Lectura)
  user = this.userInfoService.getUserSignal();
  favoriteRoutes = this.userInfoService.getRoutesSignal();
  favoriteGasStations = this.userPreferencesService.favoriteGasStations;
  userPreferences = this.userPreferencesService.userPreferences;

  // Signals de Estado Local
  selectedRouteId = signal<number | null>(null);
  selectedGasStations = signal<Set<number>>(new Set());
  sortByPrice = signal<boolean>(false);

  /**
   * Computed: Ordenación reactiva. 
   * Se recalcula automáticamente si cambia la lista, el flag de orden o el combustible preferido.
   */
  sortedStations = computed(() => {
    const stations = [...this.favoriteGasStations()];
    if (!this.sortByPrice()) return stations;

    const fuel = this.userPreferences().fuelType || 'GASOLINE';
    // Mapeo de campo de precio según combustible
    const priceField = (fuel === 'GASOLINE' || fuel === 'ALL' || fuel === 'ELECTRIC') ? 'Gasolina95' : 'Diesel';

    return stations.sort((a, b) => {
      const priceA = (a as any)[priceField] ?? Infinity;
      const priceB = (b as any)[priceField] ?? Infinity;
      return priceA - priceB;
    });
  });

  // --- Gestión de Rutas ---

  toggleRouteSelection(routeId: number): void {
    this.selectedRouteId.update(id => id === routeId ? null : routeId);
  }

  deleteRoute(route: any): void {
    if (confirm(this.translation.translate('userInfo.confirmDelete') + ` "${route.name}"?`)) {
      this.userInfoService.deleteRoute(route.routeId).subscribe({
        next: () => {
          // Actualizamos la señal global (esto actualiza el localStorage automáticamente en el servicio)
          this.userInfoService.setRoutes(this.favoriteRoutes().filter(r => r.routeId !== route.routeId));
          this.selectedRouteId.set(null);
        },
        error: (err) => console.error('Error delete:', err)
      });
    }
  }

  renameRoute(route: any): void {
    const newName = prompt(this.translation.translate('userInfo.promptRename'), route.name);
    if (newName && newName.trim() && newName.trim() !== route.name) {
      this.userInfoService.renameRoute(route.routeId, newName.trim()).subscribe({
        next: () => {
          this.userInfoService.setRoutes(this.favoriteRoutes().map(r =>
            r.routeId === route.routeId ? { ...r, name: newName.trim() } : r
          ));
        },
        error: (err) => console.error('Error rename:', err)
      });
    }
  }

  // --- Gestión de Gasolineras ---

  toggleGasStationSelection(stationId: number): void {
    this.selectedGasStations.update(set => {
      const newSet = new Set(set);
      if (newSet.has(stationId)) newSet.delete(stationId);
      else newSet.add(stationId);
      return newSet;
    });
  }

  toggleSortByPrice(): void {
    this.sortByPrice.update(v => !v);
  }

  // --- Helpers ---

  getOrigin(route: any): string {
    return route.points?.find((p: any) => p.type === 'ORIGIN')?.address || this.translation.translate('userInfo.na');
  }

  getDestination(route: any): string {
    return route.points?.find((p: any) => p.type === 'DESTINATION')?.address || this.translation.translate('userInfo.na');
  }

  getStationType(type: string): string {
    const types: Record<string, string> = {
      'A': 'Autoservicio',
      'S': 'Servicio Asistido',
      'P': 'Convencional',
      'R': 'Convencional'
    };
    return types[type] || type;
  }

}