import { Component, inject, signal, OnInit, computed, effect, ViewChild, ElementRef } from '@angular/core';
import { RouteFormResponse } from '../../../Dto/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { SearchBarService } from '../../../services/search-bar/search-bar.service';
import { NgClass } from '@angular/common';
import { RouteService } from '../../../services/routes/route.service';
import { TranslationService } from '../../../services/translation.service';
import { FavouriteGasStation, GasStation } from '../../../Dto/gas-station';
import { UserInfoService } from '../../../services/user-page/user-info.service';
import { UserPreferencesService } from '../../../services/user-page/user-preferences.service';
import { AuthService } from '../../../services/auth/auth-service.service';
import { AuthGuard } from '../../../guards/auth.guard';
import { MapCommunicationService } from '../../../services/map/map-communication.service';
import { LoginPromptComponent } from '../../components/search-bar-components/login-prompt/login-prompt.component';
import { LoginPromptService } from '../../../services/login-prompt/login-prompt.service';
import { SavedRouteDto, RoutePreferencesDto } from '../../../Dto/user-dtos';
import { SearchBarTabsComponent } from '../../components/search-bar-components/search-bar-tabs/search-bar-tabs.component';
import { SearchBarFiltersComponent } from '../../components/search-bar-components/search-bar-filters/search-bar-filters.component';
import { SearchBarFormComponent } from '../../components/search-bar-components/search-bar-form/search-bar-form.component';

@Component({
  selector: 'app-search-bar',
  imports: [MapPageComponent, NgClass, LoginPromptComponent, SearchBarTabsComponent, SearchBarFiltersComponent, SearchBarFormComponent],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit {

  isLoggedIn = signal<boolean>(false);
  isFormCollapsed: boolean = false;
  showShareMessage = signal(false);
  createdRoute = signal(false);
  loginPromptService = inject(LoginPromptService);

  allGasStations = signal<GasStation[]>([]);
  filterByBrands = signal<boolean>(false);
  filterByCheapest = signal<boolean>(false);
  filterByMaxPrice = signal<boolean>(false);

  translation = inject(TranslationService);
  userInfo = inject(UserInfoService);
  userPreferences = inject(UserPreferencesService);

  favouriteGasStations = signal<FavouriteGasStation[]>([]);
  savedRoute = signal<SavedRouteDto[]>([]);
  preferredBrands: string[] = [];
  fuelType: string = 'ALL';
  maxPrice: number = 0;
  radioKm: number = 2;
  destinationType: string = "";
  routeAlias: string = "";
  selectedRouteOption = {
    origin: "",
    destination: ""
  };

  routeFormResponse: RouteFormResponse = {
    origin: "",
    destination: "",
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute: false,
    avoidTolls: false
  };
  waypointTypes: string[] = [];

  destinationTypeOptions: Record<string, string> = {
    coordinates: "Destino",
    favouriteGasStations: "Gasolinera favorita",
    savedRoute: "Ruta guardada"
  };

  vehiculeEmissionTypeOptions: Record<string, string> = {
    ELECTRIC: "Eléctrico",
    HYBRID: "Híbrido",
    DIESEL: 'Diesel',
    GASOLINE: "Gasolina"
  };

  activeTab: string = 'destination';
  selectedSavedRoute: number | null = null;

  @ViewChild('card', { static: true }) card!: ElementRef;
  @ViewChild('formWrapper') formWrapper!: ElementRef;

  scrollToCard() {
    if (window.innerWidth >= 768) {
      const rect = this.card.nativeElement.getBoundingClientRect();
      const offset = 205;
      window.scrollTo({
        top: window.scrollY + rect.top + offset,
        behavior: 'smooth'
      });
    }
  }

  constructor(private searchBarService: SearchBarService, private routeService: RouteService, private authService: AuthService, private authGuard: AuthGuard, private mapCommunication: MapCommunicationService) {
    effect(() => {
      this.mapCommunication.sendGasStations(this.filteredGasStations());
    });
  }

  ngOnInit(): void {
    this.authGuard.isLoggedIn().subscribe(logged => {
      this.authService.sendUserSession(logged);
    });

    this.authService.getUserSession().subscribe(logged => {
      this.isLoggedIn.set(logged);
      if (logged) {
        this.userPreferences.getUserPreferences().subscribe((pref: RoutePreferencesDto) => {
          this.routeFormResponse.avoidTolls = pref.avoidTolls;
          this.preferredBrands = pref.preferredBrands;
          this.fuelType = pref.fuelType;
          this.maxPrice = pref.maxPrice;
          this.radioKm = pref.radioKm;
          this.routeFormResponse.radioKm = pref.radioKm;
        });
        this.searchBarService
          .saveFavouriteGasStations()
          .subscribe(gas => {
            const parsedGas = JSON.parse(gas) as FavouriteGasStation[];
            console.log('Gasolineras parseadas:', parsedGas);
            this.favouriteGasStations.set(parsedGas);
          });
        this.searchBarService
          .saveSavedRoutes()
          .subscribe(route => {
            try {
              const parsedRoutes = JSON.parse(route) as SavedRouteDto[];
              this.savedRoute.set(parsedRoutes);
            } catch (e) {
            }
          });
      } else {
        this.favouriteGasStations.set([]);
        this.savedRoute.set([]);
        this.fuelType = 'ALL';
        this.maxPrice = 0;
        this.radioKm = 2;
        this.routeFormResponse.radioKm = 2;
      }
    });
  }

  setTab(tab: string) {
    if (tab === 'gas' || tab === 'route') {
      if (!this.isLoggedIn()) {
        this.loginPromptService.openLoginPrompt();
        return;
      }
    }
    this.activeTab = tab;
  }

  shareRoute() {
    // Lógica futura: generar link de la ruta
    // Por ahora, copia la URL actual
    navigator.clipboard.writeText(window.location.href).then(() => {
      this.showShareMessage.set(true);
      setTimeout(() => this.showShareMessage.set(false), 2000);
    });
  }

  addWaypoint() {
    if (this.routeFormResponse.waypoints.length < 5) {
      this.routeFormResponse.waypoints.push('');
      this.waypointTypes.push('text');
    }
  }

  deleteWaypoint() {
    this.routeFormResponse.waypoints.pop();
    this.waypointTypes.pop();
  }

  savedRouteSelected() {
    this.routeFormResponse.origin = this.selectedRouteOption.origin;
    this.routeFormResponse.destination = this.selectedRouteOption.destination;
  }

  submitted: boolean = false;
  onSubmit() {
    this.submitted = true;
    if (this.activeTab === 'route') {
      const selected = this.selectedSavedRoute;
      if (selected) {
        const route = this.savedRoute().find(r => r.routeId == selected);
        if (route) {
          if (route.points && Array.isArray(route.points)) {
            const originPoint = route.points.find(p => p.type === 'ORIGIN');
            const destPoint = route.points.find(p => p.type === 'DESTINATION');
            const waypoints = route.points.filter(p => p.type === 'WAYPOINT').map(p => p.address);
            this.routeFormResponse.origin = originPoint?.address || '';
            this.routeFormResponse.destination = destPoint?.address || '';
            this.routeFormResponse.waypoints = waypoints;
            this.routeFormResponse.optimizeWaypoints = route.optimizeWaypoints ?? false;
            this.routeFormResponse.optimizeRoute = route.optimizeRoute ?? false;
            this.routeFormResponse.avoidTolls = route.avoidTolls ?? false;
          } else {
            this.successfulMessage = "";
            return;
          }
        } else {
          this.errorMessage = this.translation.translate('search.routeNotFound');
          this.successfulMessage = "";
          return;
        }
      } else {
        this.errorMessage = this.translation.translate('search.noRouteSelected');
        this.successfulMessage = "";
        return;
      }
    }
    this.searchBarService.onSubmit(this.routeFormResponse).subscribe({
      next: (gasStations) => {
        console.log('onSubmit exitoso, gasStations:', gasStations);
        this.allGasStations.set(gasStations);
        this.createdRoute.set(true);
      },
      error: (err) => {
        console.error('Error en onSubmit:', err);
      }
    });
  }

  filteredGasStations = computed(() => {
    let stations = this.allGasStations();
    if (this.isLoggedIn() && this.filterByBrands()) {
      stations = stations.filter(station =>
        this.preferredBrands.some(brand => brand.toLowerCase() === station.marca.toLowerCase())
      );
    }
    if (this.filterByCheapest()) {
      const fuelKey = (this.fuelType === 'ALL' || this.fuelType === 'GASOLINE') ? 'Gasolina95' : 'Diesel';
      const cheapest = stations.reduce((min, station) => {
        const price = station[fuelKey];
        return price != null && (min.price == null || price < min.price) ? { station, price } : min;
      }, { station: null as GasStation | null, price: null as number | null });
      stations = cheapest.station ? [cheapest.station] : [];
    }
    if (this.isLoggedIn() && this.filterByMaxPrice()) {
      const fuelKey = (this.fuelType === 'ALL' || this.fuelType === 'GASOLINE') ? 'Gasolina95' : 'Diesel';
      stations = stations.filter(station => {
        const price = station[fuelKey];
        return price != null && price <= this.maxPrice;
      });
    }
    return stations;
  });

  trackByIndex(index: number) {
    return index;
  }

  toggleFilterByBrands() {
    this.filterByBrands.update(v => !v);
  }

  toggleFilterByCheapest() {
    this.filterByCheapest.update(v => !v);
  }

  toggleFilterByMaxPrice() {
    this.filterByMaxPrice.update(v => !v);
  }

  successfulMessage: string = "";
  errorMessage: string = "";
  saveRoute() {
    this.searchBarService.saveFavouriteRoute(this.routeAlias, this.routeFormResponse)
      .subscribe({
        next: (response) => {
          this.savedRoute.update(routes => [...routes, response as SavedRouteDto]);
          this.successfulMessage = this.translation.translate('search.routeSaved');
          this.errorMessage = "";
          this.routeAlias = "";
        }, error: (err) => {
          this.errorMessage = this.translation.translate('search.saveError');
          this.successfulMessage = "";
          console.log(err);
        },
      });
  }

  toggleFormCollapse() {
    this.isFormCollapsed = !this.isFormCollapsed;
    if (this.isFormCollapsed && typeof document !== 'undefined') {
      const formWrapper = document.querySelector('.form-wrapper') as HTMLElement;
      if (formWrapper) {
        formWrapper.scrollTop = 0;
      }
    }
  }

  isDesktop(): boolean {
    return typeof window !== 'undefined' && window.innerWidth >= 768;
  }

}