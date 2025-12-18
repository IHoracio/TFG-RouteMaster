import { Injectable } from '@angular/core';
import { RouteFormResponse } from '../../features/pages/map-page/Utils/route-form-response';
import { RouteService } from '../routes/route.service';
import { Coords, RouteGroupResponse } from '../../Dto/maps-dtos';
import { MapCommunicationService } from '../map/map-communication.service';

@Injectable({
  providedIn: 'root'
})
export class SearchBarService {

  constructor(private routeService: RouteService, private mapCommunication: MapCommunicationService) { }

  onSubmit(routeFormResponse: RouteFormResponse) {
    /*console.log(routeFormResponse)
    let message: RouteGroupResponse = {
    routes: []
    };
    let cadena: string = ""
    this.routeService.calculateRoute(routeFormResponse)
      .subscribe(data => {
        message = JSON.parse(data)
        console.log(message)
        
      })
      */
      this.saveCoordinates(routeFormResponse);
      setTimeout(() => {
        console.log('sleep');
        this.saveWaypointCoordinates(routeFormResponse);
      }, 1000);
      
  }

  saveCoordinates(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []

    this.routeService.calculateCoords(routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);

      coords = parsedData;
      this.giveCoords(coords)
    })
  }
  saveWaypointCoordinates(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []

    this.routeService.calculateLegCoords(routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);
      coords = parsedData;
      this.giveWaypointCoords(coords)
    })
    
  }
  giveCoords(coords: Coords[]){
    this.mapCommunication.sendRoute(coords)
  }
  giveWaypointCoords(coords: Coords[]){
    console.log("coordenadas give waypointCoords", coords)
    this.mapCommunication.sendPoints(coords);
  }
}
