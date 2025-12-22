import { Injectable, TemplateRef } from '@angular/core';
import { RouteFormResponse } from '../../Dto/route-form-response';
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
     this.saveCoordinates(routeFormResponse)
     setTimeout(()=>{
        this.saveWaypointCoordinates(routeFormResponse)    
     }, 1000)
    this.saveGasStationsCoordinates(routeFormResponse)
  }

  saveCoordinates(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []

    this.routeService.calculatePolylineCoords(routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);

      coords = parsedData;
      this.giveCoords(coords)
    })
  }
  saveWaypointCoordinates(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []

    this.routeService.calculatePointCoords(routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);
      coords = parsedData;
      this.giveWaypointCoords(coords)
    })
    
  }
  saveGasStationsCoordinates(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []
    this.routeService.calculateGasStations(routeFormResponse)
    .subscribe(data => {
      console.log("gasolinera", data)
      const parsedData = JSON.parse(data);
      coords = parsedData;
      console.log(coords)
      this.giveGasStationCoords(coords)
    })
    
  }

  giveCoords(coords: Coords[]){
    this.mapCommunication.sendRoute(coords)
  }
  giveWaypointCoords(coords: Coords[]){
    this.mapCommunication.sendPoints(coords);
  }
  giveGasStationCoords(coords: Coords[]){
    this.mapCommunication.sendGasStations(coords)
  }
}
