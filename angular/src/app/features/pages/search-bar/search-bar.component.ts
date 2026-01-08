import { Component} from '@angular/core';
import { FormsModule} from '@angular/forms';
import { RouteFormResponse } from '../../../Dto/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { SearchBarService } from '../../../services/search-bar/search-bar.service';
import { NgFor } from '@angular/common';
import { RouteService } from '../../../services/routes/route.service';
import { RouteGroupResponse } from '../../../Dto/maps-dtos';
import { UserDto} from '../../../Dto/user-dtos';



@Component({
  selector: 'app-search-bar',
  imports: [FormsModule, MapPageComponent, NgFor],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

  userDto: UserDto = {
  id: 0,
  email: '',
  password: '',
  passwordConfirmation: '',
  name: '',
  surname: '',

  userPreferences: {
    theme: '',
    language: '',
    user: ''
  },

  savedRoutes: [],

  favouriteGasStations: [],

  gasStationPriority: 'PRICE',

  routePreferences: {
    preferredBrands: [],
    radioKm: 0,
    fuelType: '',
    maxPrice: 0,
    mapView: 'SATELLITE'
  }
};


  destinationType: string = "";
  routeFormResponse: RouteFormResponse = {
    origin : "",
    destination : "",
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute : false
  }
  
  constructor(private searchBarService: SearchBarService, private routeService: RouteService) {
    this.initializeUser()
  }
  
  initializeUser() {
  this.searchBarService
    .saveUserData('lorenzoboda01@gmail.com')
    .subscribe(user => {
      this.userDto = JSON.parse(user);
      console.log(this.userDto);
    });
  }
  addWaypoint(){
    this.routeFormResponse.waypoints.push('')
  }
  deleteWaypoint(){
    this.routeFormResponse.waypoints.pop()
  }
  message: RouteGroupResponse = {
      routes: []
  };

  onSubmit() {
      this.searchBarService.onSubmit(this.routeFormResponse)
      console.log(this.routeFormResponse)
    }
  trackByIndex(index: number) {
  return index;
}
}
