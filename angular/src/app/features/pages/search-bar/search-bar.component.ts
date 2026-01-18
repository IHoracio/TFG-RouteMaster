import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteFormResponse } from '../../../Dto/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { SearchBarService } from '../../../services/search-bar/search-bar.service';
import { KeyValuePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { RouteService } from '../../../services/routes/route.service';
import { RouteGroupResponse } from '../../../Dto/maps-dtos';
import { FavouriteGasStation } from '../../../Dto/gas-station';
import { SavedRoute } from '../../../Dto/saved-route';
import { Parser } from '@angular/compiler';
import { TranslationService } from '../../../services/translation.service';



@Component({
  selector: 'app-search-bar',
  imports: [FormsModule, MapPageComponent, NgFor, KeyValuePipe, NgIf, NgClass],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

  translation = inject(TranslationService)

  favouriteGasStations = signal<FavouriteGasStation[]>([]);
  savedRoute = signal<SavedRoute[]>([])
  destinationType: string = "";
  routeAlias: string = "";
  selectedRouteOption = {
    origin: "",
    destination: ""
  }

  routeFormResponse: RouteFormResponse = {
    origin: "",
    destination: "",
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute: false,
    avoidTolls: false,
    vehiculeEmissionType: "DIESEL"
  }
  waypointTypes: string[] = [];



  destinationTypeOptions: Record<string, string> = {
    coordinates: "Destino",
    favouriteGasStations: "Gasolinera favorita",
    savedRoute: "Ruta guardada"
  }

  vehiculeEmissionTypeOptions: Record<string, string> = {
    ELECTRIC: "Eléctrico",
    HYBRID: "Híbrido",
    DIESEL: 'Diesel',
    GASOLINE: "Gasolina"
  }


  activeTab: string = 'destination';


  setTab(tab: string) {
    this.activeTab = tab;
  }
  constructor(private searchBarService: SearchBarService, private routeService: RouteService) {
    //this.initializeUser()
  }
  ngOnInit(): void {
    this.initializeUser();
  }

  initializeUser() {
    this.searchBarService
      .saveFavouriteGasStations()
      .subscribe(gas => this.favouriteGasStations.set(JSON.parse(gas))
      );

    this.searchBarService
      .saveSavedRoutes()
      .subscribe(route => {
        console.log(route)
        this.savedRoute.set(JSON.parse(route))
      })
  }
  addWaypoint() {
    this.routeFormResponse.waypoints.push('')
    this.waypointTypes.push('text');
  }
  deleteWaypoint() {
    this.routeFormResponse.waypoints.pop()
    this.waypointTypes.pop();
  }
  savedRouteSelected() {
    this.routeFormResponse.origin = this.selectedRouteOption.origin
    this.routeFormResponse.destination = this.selectedRouteOption.destination
  }



  submitted: boolean = false;
  onSubmit() {
    console.log(this.routeFormResponse)
    this.searchBarService.onSubmit(this.routeFormResponse)
    this.submitted = true;
    this.initializeUser()

  }
  trackByIndex(index: number) {
    return index;
  }

  successfulMessage: string = "";
  errorMessage: string = "";
  saveRoute() {
    this.searchBarService.saveFavouriteRoute(this.routeAlias, this.routeFormResponse)
      .subscribe({
        next: (response) => {
          this.successfulMessage = "Se ha guardado la ruta como favorita."
          this.errorMessage = "";
          console.log(response)
        }, error: (err) => {
          this.errorMessage = "Ha occurido un error."
          this.successfulMessage = ""
          console.log(err)
        },
      })
  }
}
