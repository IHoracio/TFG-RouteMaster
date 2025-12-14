import { Component, Input, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../../../services/routes/route.service';
import { Coords, RouteGroupResponse } from '../map-page/Utils/google-route.mapper';

import { RouteFormResponse } from '../map-page/Utils/route-form-response';
import { MapPageComponent } from '../map-page/map-page.component';
import { MapCommunicationService } from '../../../services/map/map-communication.service';


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
  private coords: Coords[] = []
  

  onSubmit() {
    console.log(this.routeFormResponse)

    this.routeService.calculateRoute(this.routeFormResponse)
      .subscribe(data => this.message = data);

      this.guardarCoordenadas();
  }

  guardarCoordenadas(){
    this.coords = []

    this.routeService.calculateCoords(this.routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);

      this.coords = parsedData;
      this.giveCoords()
    })
  }

  imprimirMensaje() {
    console.log(JSON.stringify(this.message));
    console.log(this.coords)
  }

  giveCoords(){
    this.mapCommunication.sendRoute(this.coords)
  }

}
