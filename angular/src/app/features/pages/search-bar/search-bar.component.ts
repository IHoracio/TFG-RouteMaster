import { Component, Input, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../../../services/routes/route.service';
import { extractAllCoords, RouteGroupResponse } from '../map-page/Utils/google-route.mapper';
import { Coords } from '../map-page/Utils/google-route.mapper';

@Component({
  selector: 'app-search-bar',
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {


  constructor(private routeService: RouteService) {
  }

  origin: string = ""
  destination: string = ""

  message: RouteGroupResponse = {
    routes: []
  };

  coords: Coords [] = []

  onSubmit() {
    console.log(this.origin, this.destination)
    
    this.routeService.calculateRoute(this.origin, this.destination)
      .subscribe(data => this.message = JSON.parse(data));
  }

  guardameLasCoordenadasPapi(){
    this.coords =  extractAllCoords(this.message)
  }

  imprimirMensaje() {
    console.log(JSON.stringify(this.message));
    console.log(this.coords)
  }

  

}
