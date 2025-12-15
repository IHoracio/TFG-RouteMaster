import { Component, Input, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteFormResponse } from '../map-page/Utils/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { SearchBarService } from '../../../services/search-bar/search-bar.service';



@Component({
  selector: 'app-search-bar',
  imports: [FormsModule, MapPageComponent],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {


  constructor(private searchBarService: SearchBarService) {
    
  }

  routeFormResponse: RouteFormResponse = {
    origin : "",
    destination : "",
    optimizeRoute : false
  }
  onSubmit(){
    this.searchBarService.onSubmit(this.routeFormResponse)
  }
}
