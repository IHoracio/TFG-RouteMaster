import { Component, signal, inject, computed, ChangeDetectionStrategy } from '@angular/core';

import { Router } from '@angular/router';
import { UserPreferencesService } from '../../../../services/user-page/user-preferences.service';
import { FavouriteGasStation } from '../../../../Dto/gas-station';
import { UserInfoService } from '../../../../services/user-page/user-info.service';
import { TranslationService } from '../../../../services/singleton/translation.service';
import { GasStationService } from '../../../../services/user-page/gas-station/gas-station.service';
import { catchError, forkJoin, of } from 'rxjs';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-user-info',
  imports: [],
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent {

  translation = inject(TranslationService);

  private userInfoService = inject(UserInfoService);
  private userPreferencesService = inject(UserPreferencesService);
  private gasStationService = inject(GasStationService);

  // Signals Globales (Lectura)
  user = this.userInfoService.getUserSignal();
  favoriteRoutes = this.userInfoService.getRoutesSignal();
  favoriteGasStations = this.userPreferencesService.favoriteGasStations;
  userPreferences = this.userPreferencesService.userPreferences;

  // Signals de Estado Local
  selectedRouteId = signal<number | null>(null);
  selectedGasStations = signal<Set<number>>(new Set());
  sortByPrice = signal<boolean>(false);
  filterByMaxPrice = signal<boolean>(false);
  filterByBrand = signal<boolean>(false);
  activeSection = signal<string | null>(null);

  ngOnInit(): void {
    this.refreshFavoritePrices();
  }

  // Selectores de sección
  toggleSection(section: string): void {
    this.activeSection.update(v => v === section ? null : section);
  }

  private refreshFavoritePrices(): void {
    const currentFavorites = this.favoriteGasStations();

    if (currentFavorites.length === 0) return;

    // 1. Creamos un array de observables (peticiones)
    const requests = currentFavorites.map(station =>
      this.gasStationService.getGasStation(station.idEstacion).pipe(
        // Si una gasolinera falla, devolvemos null para no romper el forkJoin
        catchError(err => {
          console.error(`Error cargando precio de ${station.idEstacion}`, err);
          return of(null);
        })
      )
    );

    // 2. Ejecutamos todas en paralelo
    forkJoin(requests).subscribe(freshDataArray => {
      const updatedStations = currentFavorites.map((oldStation, index) => {
        const freshData = freshDataArray[index];

        if (!freshData) return oldStation; // Si falló la carga, mantenemos lo que había

        // 3. Fusionamos los datos frescos (precios) con el alias y placeSelection
        return {
          ...oldStation, // Mantenemos Alias y PlaceSelection
          ...freshData,  // Sobreescribimos con precios y datos actualizados de la API
        } as FavouriteGasStation;
      });

      // 4. Actualizamos la señal global (y por tanto el LocalStorage)
      this.userPreferencesService.updateData(
        this.userPreferencesService.favoriteGasStations,
        'favoriteGasStations',
        updatedStations
      );
    });
  }

  /**
     * Computed: Lista final procesada.
     * Consumimos las señales centralizadas del servicio según los interruptores locales.
     */
  sortedStations = computed(() => {
    // 1. ¿Qué base usamos? ¿La ordenada por precio o la normal?
    let stations = this.sortByPrice()
      ? [...this.userPreferencesService.sortedFavoritesByPrice()]
      : [...this.favoriteGasStations()];

    // 2. ¿Filtramos por marca? Si el botón está activo, usamos la lógica del servicio
    if (this.filterByBrand()) {
      const preferredBrands = this.userPreferences().preferredBrands || [];
      if (preferredBrands.length > 0) {
        // Aquí es donde el servicio hace el trabajo
        stations = stations.filter(s =>
          preferredBrands.some((b: string) => b.toUpperCase().trim() === s.marca?.toUpperCase().trim())
        );
      }
    }

    // 3. ¿Filtramos por precio máximo
    if (this.filterByMaxPrice()) {
      const maxPrice = this.userPreferences().maxPrice;
      const fuel = this.userPreferences().fuelType || 'ALL';
      const priceField = this.getPriceField(fuel);

      if (maxPrice > 0) {
        stations = stations.filter(s => {
          const price = (s as any)[priceField];
          return price !== null && price <= maxPrice;
        });
      }
    }

    return stations;
  });

  // Métodos para los botones
  toggleMaxPriceFilter(): void {
    this.filterByMaxPrice.update(v => !v);
  }

  toggleBrandFilter(): void {
    this.filterByBrand.update(v => !v);
  }

  // Helper privado para el mapeo (puedes moverlo a una constante si prefieres)
  private getPriceField(fuel: string): string {
    const map: any = {
      'ALL': 'Gasolina95', 'GASOLINE_95': 'Gasolina95', 'GASOLINE_98': 'Gasolina98',
      'DIESEL': 'Diesel', 'DIESEL_PREMIUM': 'DieselPremium', 'GLP': 'GLP'
    };
    return map[fuel] || 'Gasolina95';
  }

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

  // Comprueba si la ruta tiene paradas intermedias
  hasWaypoints(route: any): boolean {
    return route.points?.some((p: any) => p.type === 'WAYPOINT');
  }

  // Ahora devuelve un array de strings en lugar de un solo string
  getWaypoints(route: any): string[] {
    if (!route.points) return [];

    return route.points
      .filter((p: any) => p.type === 'WAYPOINT')
      .map((p: any) => p.placeSelection?.address || this.translation.translate('userInfo.na'));
  }

  getOrigin(route: any): string {
    // Añadimos el "?" después de placeSelection por seguridad
    return route.points?.find((p: any) => p.type === 'ORIGIN')?.placeSelection?.address
      || this.translation.translate('userInfo.na');
  }

  getDestination(route: any): string {
    // Añadimos el "?" después de placeSelection por seguridad
    return route.points?.find((p: any) => p.type === 'DESTINATION')?.placeSelection?.address
      || this.translation.translate('userInfo.na');
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

  isSectionActive(section: string): boolean {
    return this.activeSection() === section;
  }

}