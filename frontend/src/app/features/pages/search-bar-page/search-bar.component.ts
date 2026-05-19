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
import { FullRouteData, PuntosDTO } from '../../../Dto/full-route-data';

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

    const fuelPriceMap: Record<string, string> = {
      'ALL': 'Gasolina95',
      'GASOLINE_95': 'Gasolina95',
      'GASOLINE_98': 'Gasolina98',
      'DIESEL': 'Diesel',
      'DIESEL_PREMIUM': 'DieselPremium',
      'GLP': 'GLP'
    };

    const fuelType = prefs.fuelType || 'GASOLINE';
    const fuelKey = fuelPriceMap[fuelType] || 'Gasolina95';

    if (this.isLoggedIn() && this.filterByBrands()) {
      const brands = prefs.preferredBrands || [];
      stations = stations.filter(s => brands.some((b: string) => b.toLowerCase() === s.marca.toLowerCase()));
    }

    if (this.filterByCheapest()) {
      const cheapest = stations.reduce((min, station) => {
        const price = (station as any)[fuelKey];
        return price != null && (min.price == null || price < min.price) ? { station, price } : min;
      }, { station: null as GasStation | null, price: null as number | null });
      stations = cheapest.station ? [cheapest.station] : [];
    }

    if (this.isLoggedIn() && this.filterByMaxPrice()) {
      const maxPrice = prefs.maxPrice || 0;
      stations = stations.filter(s => {
        const price = (s as any)[fuelKey];
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

  // En el componente de tu SearchBar

  onSubmit() {
    const routeId = this.selectedSavedRouteId();

    // CASO A: Ejecutar una ruta guardada previamente
    if (this.activeTab() === 'route' && routeId) {

      this.routeService.executeSavedRoute(routeId).subscribe({
        next: (fullData: FullRouteData) => {
          // 1. Enviamos los datos al mapa mediante el servicio de comunicación
          this.mapCommunication.sendRoute(fullData.polylineCoords);
          this.mapCommunication.sendPoints(fullData.legCoords);
          this.mapCommunication.sendGasStations(fullData.gasStations);
          this.mapCommunication.sendWeather(fullData.weatherData || []);

          // 2. Actualizamos distancia y duración (para que el InfoWindow y otros los vean)
          this.mapCommunication.sendTotalDistance(fullData.totalDistance);
          this.mapCommunication.sendTotalDuration(fullData.totalDuration);

          // 3. Estado de la UI
          this.allGasStations.set(fullData.gasStations);
          this.createdRoute.set(true);
        },
        error: (err) => {
          this.errorMessage.set(this.translation.translate('search.loadError') || 'Error al cargar ruta');
          console.error('Error al ejecutar ruta guardada:', err);
        }
      });

    }
    // CASO B: Búsqueda fresca (Origen y Destino manuales)
    else {
      this.searchBarService.onSubmit(this.routeFormResponse).subscribe({
        next: (gasStations) => {
          this.allGasStations.set(gasStations);
          this.createdRoute.set(true);
        },
        error: (err) => console.error('Error en búsqueda fresca:', err)
      });
    }
  }

  saveRoute() {
    if (!this.isLoggedIn()) {
      this.loginPromptService.openLoginPrompt();
    } else {
      const polylineCoords = this.mapCommunication?.getPolylineCoords() || [];
      const legCoords = this.mapCommunication?.getLegCoords() || [];
      const lang = this.translation.getCurrentLang ? this.translation.getCurrentLang() : 'es';
      const totalDistance = this.mapCommunication.getTotalDistance() || '0 Km';
      const totalDuration = this.mapCommunication.getTotalDuration() || '0 mins';


      this.searchBarService.saveFavouriteRoute(this.routeAlias(), this.routeFormResponse, polylineCoords, legCoords, lang, totalDistance, totalDuration)
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
    const totalDistance = this.mapCommunication?.getTotalDistance() || '0 km';
    const totalDUration = this.mapCommunication?.getTotalDuration() || '0 mins';
    const polylineCoords = this.mapCommunication?.getPolylineCoords() || [];
    const legCoords = this.mapCommunication?.getLegCoords() || [];
    const gasRadius = this.routeFormResponse.radioKm || 1;
    const lang = this.translation.getCurrentLang?.() || 'es';

    // 1. Construir la lista estructurada de puntos para el Backend
    const puntosDTO: PuntosDTO[] = [];

    if (this.routeFormResponse.origin) {
      puntosDTO.push({
        type: 'ORIGIN',
        placeSelection: this.routeFormResponse.origin
      });
    }

    if (this.routeFormResponse.waypoints && this.routeFormResponse.waypoints.length > 0) {
      this.routeFormResponse.waypoints.forEach(wp => {
        puntosDTO.push({
          type: 'WAYPOINT',
          placeSelection: wp
        });
      });
    }

    if (this.routeFormResponse.destination) {
      puntosDTO.push({
        type: 'DESTINATION',
        placeSelection: this.routeFormResponse.destination
      });
    }

    this.routeService.shareRoute(totalDistance, totalDUration, puntosDTO, polylineCoords, legCoords, gasRadius, lang).subscribe({
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
        // 1. Enviar datos geométricos y de entorno al mapa
        if (data.polylineCoords) this.mapCommunication.sendRoute(data.polylineCoords);
        if (data.legCoords) this.mapCommunication.sendPoints(data.legCoords);
        if (data.gasStations) this.mapCommunication.sendGasStations(data.gasStations);
        if (data.weatherData) this.mapCommunication.sendWeather(data.weatherData);

        // Hidratamos distancia y duración en el mapa para que se vean los datos de la ruta
        if (data.totalDistance) this.mapCommunication.sendTotalDistance(data.totalDistance);
        if (data.totalDuration) this.mapCommunication.sendTotalDuration(data.totalDuration);

        // 2. Extraer y procesar los puntos semánticos del DTO
        const puntos = data.puntosDTO || [];
        const origin = puntos.find(p => p.type === 'ORIGIN')?.placeSelection || null;
        const destination = puntos.find(p => p.type === 'DESTINATION')?.placeSelection || null;
        const waypoints = puntos.filter(p => p.type === 'WAYPOINT').map(p => p.placeSelection);

        this.waypointTypes = waypoints.map(() => 'text');

        // 3. Asignar los datos al objeto del formulario (aunque la UI no los pinte en 'destination')
        // Se quedan guardados en memoria para cuando el usuario pulse 'Guardar Favorita'
        this.routeFormResponse = {
          origin: origin,
          destination: destination,
          waypoints: [...waypoints],
          optimizeWaypoints: this.routeFormResponse.optimizeWaypoints,
          optimizeRoute: this.routeFormResponse.optimizeRoute,
          avoidTolls: this.routeFormResponse.avoidTolls,
          radioKm: this.routeFormResponse.radioKm
        };

        // 4. Activar estados de renderizado de gasolineras
        this.allGasStations.set(data.gasStations || []);
        this.createdRoute.set(true);

        // 5. TRUCO DE LA PESTAÑA: Forzar el cambio al tab 'route' 
        // Oculta el formulario manual y muestra directamente las opciones de rutas guardadas/favoritas
        this.activeTab.set('route');
      },
      error: (err) => console.error('Error al cargar ruta compartida:', err)
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
  handleOriginSelected(selection: PlaceSelection) {
    this.routeFormResponse.origin = selection;
  }

  handleDestinationSelected(selection: PlaceSelection) {
    this.routeFormResponse.destination = selection;
  }

  handleWaypointSelected(index: number, selection: PlaceSelection) {
    this.routeFormResponse.waypoints[index] = selection;
  }

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