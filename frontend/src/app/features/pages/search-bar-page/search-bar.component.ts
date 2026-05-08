import { Component, inject, signal, OnInit, computed, effect, ViewChild, ElementRef, output } from '@angular/core';
import { RouteFormResponse } from '../../../Dto/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { SearchBarService } from '../../../services/search-bar/search-bar.service';
import { NgClass } from '@angular/common';
import { RouteService } from '../../../services/routes/route.service';
import { GasStation } from '../../../Dto/gas-station';
import { UserInfoService } from '../../../services/user-page/user-info.service';
import { UserPreferencesService } from '../../../services/user-page/user-preferences.service';
import { AuthService } from '../../../services/auth/auth-service.service';
import { MapCommunicationService } from '../../../services/map/map-communication.service';
import { LoginPromptComponent } from '../../components/search-bar-components/login-prompt/login-prompt.component';
import { LoginPromptService } from '../../../services/login-prompt/login-prompt.service';
import { SavedRouteDto } from '../../../Dto/user-dtos';
import { SearchBarTabsComponent } from '../../components/search-bar-components/search-bar-tabs/search-bar-tabs.component';
import { SearchBarFiltersComponent } from '../../components/search-bar-components/search-bar-filters/search-bar-filters.component';
import { SearchBarFormComponent } from '../../components/search-bar-components/search-bar-form/search-bar-form.component';
import { ActivatedRoute } from '@angular/router';
import { PlaceSelection } from '../../../Dto/place-selection';
import { TranslationService } from '../../../services/singleton/translation.service';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [MapPageComponent, NgClass, LoginPromptComponent, SearchBarTabsComponent, SearchBarFiltersComponent, SearchBarFormComponent],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit {
  // Inyecciones
  private searchBarService = inject(SearchBarService);
  private routeService = inject(RouteService);
  private authService = inject(AuthService);
  private mapCommunication = inject(MapCommunicationService);
  private activatedRoute = inject(ActivatedRoute);

  translation = inject(TranslationService);
  userInfoService = inject(UserInfoService);
  userPrefsService = inject(UserPreferencesService);
  loginPromptService = inject(LoginPromptService);

  // Salidas
  originSelected = output<PlaceSelection>();
  destinationSelected = output<PlaceSelection>();
  waypointSelected = output<{ index: number; selection: PlaceSelection }>();

  // --- SIGNALS DE ESTADO LOCAL
  isLoggedIn = signal<boolean>(false);
  isFormCollapsed = signal<boolean>(false);
  showShareMessage = signal<boolean>(false);
  createdRoute = signal<boolean>(false);
  allGasStations = signal<GasStation[]>([]);

  // Filtros UI
  filterByBrands = signal<boolean>(false);
  filterByCheapest = signal<boolean>(false);
  filterByMaxPrice = signal<boolean>(false);

  activeTab = signal<string>('destination');
  selectedSavedRouteId = signal<string | null>(null);
  routeAlias = signal<string>("");

  // Mensajes
  successfulMessage = signal<string>("");
  errorMessage = signal<string>("");

  // --- REFERENCIAS A SIGNALS GLOBALES (Fuente única de verdad)
  favoriteGasStations = this.userPrefsService.favoriteGasStations;
  savedRoutes = this.userInfoService.getRoutesSignal();
  userPrefs = this.userPrefsService.userPreferences;

  // Modelo de formulario reactivo al estado
  routeFormResponse: RouteFormResponse = {
    origin: null,
    destination: null,
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute: false,
    avoidTolls: false,
    radioKm: 2
  };

  waypointTypes: string[] = [];

  @ViewChild('card', { static: true }) card!: ElementRef;

  constructor() {
    // Sincronización automática con el mapa cuando cambian las gasolineras filtradas
    effect(() => {
      this.mapCommunication.sendGasStations(this.filteredGasStations());
    });

    // Efecto para hidratar el formulario cuando las preferencias del usuario cambian (al loguear o resetear)
    effect(() => {
      const prefs = this.userPrefs();
      if (prefs && Object.keys(prefs).length > 0) {
        this.routeFormResponse.avoidTolls = prefs.avoidTolls ?? false;
        this.routeFormResponse.radioKm = prefs.radioKm ?? 2;
      }
    });
  }

  ngOnInit(): void {
    // Escuchar estado de sesión
    this.authService.getUserSession().subscribe(logged => {
      this.isLoggedIn.set(logged);
    });

    // Detectar ruta compartida por Token
    this.activatedRoute.paramMap.subscribe(params => {
      const token = params.get('token');
      if (token) this.loadSharedRoute(token);
    });
  }

  // --- LÓGICA DE FILTRADO (Computed)
  filteredGasStations = computed(() => {
    let stations = this.allGasStations();
    const prefs = this.userPrefs();
    const fuelType = prefs.fuelType || 'GASOLINE';
    const fuelKey = (fuelType === 'ALL' || fuelType === 'GASOLINE') ? 'Gasolina95' : 'Diesel';

    if (this.isLoggedIn() && this.filterByBrands()) {
      const brands = prefs.preferredBrands || [];
      stations = stations.filter(s => brands.some((b: string) => b.toLowerCase() === s.marca.toLowerCase()));
    }

    if (this.filterByCheapest()) {
      const cheapest = stations.reduce((min, station) => {
        const price = station[fuelKey];
        return price != null && (min.price == null || price < min.price) ? { station, price } : min;
      }, { station: null as GasStation | null, price: null as number | null });
      stations = cheapest.station ? [cheapest.station] : [];
    }

    if (this.isLoggedIn() && this.filterByMaxPrice()) {
      const maxPrice = prefs.maxPrice || 0;
      stations = stations.filter(s => {
        const price = s[fuelKey];
        return price != null && price <= maxPrice;
      });
    }

    return stations;
  });

  // --- ACCIONES
  setTab(tab: string) {
    if ((tab === 'gas' || tab === 'route') && !this.isLoggedIn()) {
      this.loginPromptService.openLoginPrompt();
      return;
    }
    this.activeTab.set(tab);
  }

  onSubmit() {
    // Si estamos en la pestaña de rutas guardadas, cargamos la configuración de la ruta
    if (this.activeTab() === 'route' && this.selectedSavedRouteId()) {
      const route = this.savedRoutes().find(r => r.routeId == this.selectedSavedRouteId());
      if (route) {
        this.routeFormResponse = {
          origin: route.points.find((p: any) => p.type === 'ORIGIN')?.placeSelection || null,
          destination: route.points.find((p: any) => p.type === 'DESTINATION')?.placeSelection || null,
          waypoints: route.points.filter((p: any) => p.type === 'WAYPOINT').map((p: any) => p.placeSelection),
          optimizeWaypoints: route.optimizeWaypoints ?? false,
          optimizeRoute: route.optimizeRoute ?? false,
          avoidTolls: route.avoidTolls ?? false,
          radioKm: route.gasRadius ?? this.userPrefs().radioKm
        };
      }
    }

    this.searchBarService.onSubmit(this.routeFormResponse).subscribe({
      next: (gasStations) => {
        this.allGasStations.set(gasStations);
        this.createdRoute.set(true);
      },
      error: (err) => console.error('Error en búsqueda:', err)
    });
  }

  saveRoute() {
    if (!this.isLoggedIn()) {
      this.loginPromptService.openLoginPrompt();
    } else {
      const polylineCoords = this.mapCommunication?.getPolylineCoords() || [];
      const legCoords = this.mapCommunication?.getLegCoords() || [];
      const lang = this.translation.getCurrentLang ? this.translation.getCurrentLang() : 'es';

      this.searchBarService.saveFavouriteRoute(this.routeAlias(), this.routeFormResponse, polylineCoords, legCoords, lang)
        .subscribe({
          next: (response) => {
            // ACTUALIZACIÓN GLOBAL Y PERSISTENTE
            const currentRoutes = this.userInfoService.getRoutesSignal()();
            this.userInfoService.setRoutes([...currentRoutes, response as SavedRouteDto]);

            this.successfulMessage.set(this.translation.translate('search.routeSaved') || 'Ruta guardada');
            this.errorMessage.set("");
            this.routeAlias.set("");
          },
          error: (err) => {
            this.errorMessage.set(this.translation.translate('search.saveError') || 'Error');
            console.error('Error al guardar ruta:', err);
          },
        });
    }
  }

  shareRoute() {
    const polylineCoords = this.mapCommunication?.getPolylineCoords() || [];
    const legCoords = this.mapCommunication?.getLegCoords() || [];
    const gasRadius = this.routeFormResponse.radioKm || 1;
    const lang = this.translation.getCurrentLang?.() || 'es';

    this.routeService.shareRoute(polylineCoords, legCoords, gasRadius, lang).subscribe({
      next: (resp) => {
        navigator.clipboard.writeText(resp.url).then(() => {
          this.showShareMessage.set(true);
          setTimeout(() => this.showShareMessage.set(false), 2000);
        });
      }
    });
  }

  private loadSharedRoute(token: string) {
    this.routeService.getSharedRoute(token).subscribe({
      next: (data) => {
        if (data.polylineCoords) this.mapCommunication.sendRoute(data.polylineCoords);
        if (data.legCoords) this.mapCommunication.sendPoints(data.legCoords);
        if (data.gasStations) this.mapCommunication.sendGasStations(data.gasStations);
        if (data.weatherData) this.mapCommunication.sendWeather(data.weatherData);
      }
    });
  }

  // --- GESTIÓN DE WAYPOINTS
  addWaypoint() {
    if (this.routeFormResponse.waypoints.length < 5) {
      this.routeFormResponse.waypoints.push(null as any);
      this.waypointTypes.push('text');
    }
  }

  deleteWaypoint() {
    this.routeFormResponse.waypoints.pop();
    this.waypointTypes.pop();
  }

  // --- HANDLERS DE UI
  handleOriginSelected(selection: PlaceSelection) { this.routeFormResponse.origin = selection; }
  handleDestinationSelected(selection: PlaceSelection) { this.routeFormResponse.destination = selection; }
  handleWaypointSelected(index: number, selection: PlaceSelection) { this.routeFormResponse.waypoints[index] = selection; }

  toggleFormCollapse() { this.isFormCollapsed.update(v => !v); }
  toggleFilterByBrands() { this.filterByBrands.update(v => !v); }
  toggleFilterByCheapest() { this.filterByCheapest.update(v => !v); }
  toggleFilterByMaxPrice() { this.filterByMaxPrice.update(v => !v); }

  scrollToCard() {
    if (window.innerWidth >= 768) {
      this.card.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
}