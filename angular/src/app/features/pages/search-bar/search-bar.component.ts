import { Component, Input, NgModule } from '@angular/core';
import { FormGroup, FormsModule, FormArray, FormControl } from '@angular/forms';
import { RouteFormResponse } from '../map-page/Utils/route-form-response';
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

  routeFormResponse: RouteFormResponse = {
    origin : "",
    destination : "",
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute : false
  }
  addWaypoint(){
    this.routeFormResponse.waypoints.push('')
    console.log(this.routeFormResponse)
  }
  deleteWaypoint(){
    this.routeFormResponse.waypoints.pop()
  }
  message: RouteGroupResponse = {
      routes: []
  };
  onSubmit() {
      console.log(this.routeFormResponse)
      this.routeService.calculateRoute(this.routeFormResponse)
        .subscribe(data => {
          this.message = JSON.parse(data)
          console.log(this.message)
        });
      this.searchBarService.saveCoordinates(this.routeFormResponse);
    }

  trackByIndex(index: number) {
  return index;
}
}
