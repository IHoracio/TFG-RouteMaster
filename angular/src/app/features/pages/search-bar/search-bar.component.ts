import { Component, Input, NgModule } from '@angular/core';
import { FormGroup, FormsModule, FormArray, FormControl } from '@angular/forms';
import { RouteFormResponse } from '../../../Dto/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { SearchBarService } from '../../../services/search-bar/search-bar.service';
import { NgFor } from '@angular/common';
import { RouteService } from '../../../services/routes/route.service';
import { RouteGroupResponse } from '../../../Dto/maps-dtos';



@Component({
  selector: 'app-search-bar',
  imports: [FormsModule, MapPageComponent, NgFor],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

  constructor(private searchBarService: SearchBarService, private routeService: RouteService) {
    
  }
  destinationType: string = "";
  routeFormResponse: RouteFormResponse = {
    origin : "",
    destination : "",
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute : false
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
    }
  trackByIndex(index: number) {
  return index;
}
}
