import { Component, Signal, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteFormResponse } from '../../../Dto/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { SearchBarService } from '../../../services/search-bar/search-bar.service';
import { KeyValuePipe, NgFor } from '@angular/common';
import { RouteService } from '../../../services/routes/route.service';
import { RouteGroupResponse } from '../../../Dto/maps-dtos';
import { FavouriteGasStationDto, UserDto } from '../../../Dto/user-dtos';
import { FavouriteGasStation, GasStation } from '../../../Dto/gas-station';



@Component({
  selector: 'app-search-bar',
  imports: [FormsModule, MapPageComponent, NgFor, KeyValuePipe],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

  favouriteGasStations = signal<FavouriteGasStation[]>([]);
  destinationType: string = "";

  routeFormResponse: RouteFormResponse = {
    origin: "",
    destination: "",
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute: false,
    avoidTolls: false,
    vehiculeEmissionType: "DIESEL"
  }

  vehiculeEmissionTypeOptions: Record<string, string> = {
    ELECTRIC: "Eléctrico",
    HYBRID: "Híbrido",
    DIESEL: 'Diesel',
    GASOLINE: "Gasolina"
  }

  constructor(private searchBarService: SearchBarService, private routeService: RouteService) {
    this.initializeUser()
  }

  initializeUser() {
    this.searchBarService
      .saveFavouriteGasStations('prueba@gmail.com')
      .subscribe(gas => {
        const parsedGas: FavouriteGasStation[] = JSON.parse(gas);
        this.favouriteGasStations.set(parsedGas)
        console.log(this.favouriteGasStations());
        console.log(parsedGas)
      });
  }
  addWaypoint() {
    this.routeFormResponse.waypoints.push('')
  }
  deleteWaypoint() {
    this.routeFormResponse.waypoints.pop()
  }
  message: RouteGroupResponse = {
    routes: []
  };


  submitted: boolean = false;
  routeAlias: string = ""
  onSubmit() {
    this.searchBarService.onSubmit(this.routeFormResponse)
    console.log(this.routeFormResponse)
    this.submitted = true;
  }
  trackByIndex(index: number) {
    return index;
  }
  saveRoute(){
    this.searchBarService.saveFavouriteRoute(this.routeAlias, "prueba@gmail.com", this.routeFormResponse)
  }
}
