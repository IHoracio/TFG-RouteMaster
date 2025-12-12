import { Component, Input, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../../../services/routes/route.service';
import { extractAllCoords, RouteGroupResponse } from '../map-page/Utils/google-route.mapper';
import { Coords } from '../map-page/Utils/google-route.mapper';
import { MapCommunicationService } from '../../../services/map/map-communication.service';
import { MapPageComponent } from '../map-page/map-page.component';
import { RouteFormResponse } from '../map-page/Utils/route-form-response';

@Component({
  selector: 'app-search-bar',
  imports: [FormsModule, MapPageComponent],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {


  constructor(private routeService: RouteService, private mapCommunication: MapCommunicationService) {
    
  }

  routeFormResponse: RouteFormResponse = {
    origin : "",
    destination : "",
    optimizeRoute : false
  }


  message: RouteGroupResponse = {
    routes: []
  };

  private coords: Coords [] = []
  

  onSubmit() {
    console.log(this.routeFormResponse)

    this.routeService.calculateRoute(this.routeFormResponse)
      .subscribe(data => this.message = JSON.parse(data));
  }

  guardameLasCoordenadasPapi(){
    this.coords =  extractAllCoords(this.message)
    this.giveCoords()
  }

  imprimirMensaje() {
    console.log(JSON.stringify(this.message));
    console.log(this.coords)
  }

  giveCoords(){
    this.mapCommunication.sendRoute(this.coords)
  }

}
