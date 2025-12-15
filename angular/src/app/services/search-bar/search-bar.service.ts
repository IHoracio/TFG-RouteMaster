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

  onSubmit(routeFormResponse: RouteFormResponse): RouteGroupResponse {
    console.log(routeFormResponse)
    let message: RouteGroupResponse = {
    routes: []
    };



    this.routeService.calculateRoute(routeFormResponse)
      .subscribe(data => message = data);



    this.guardarCoordenadas(routeFormResponse);
    return message;
  }

  guardarCoordenadas(routeFormResponse: RouteFormResponse){
    let coords: Coords[] = []

    this.routeService.calculateCoords(routeFormResponse)
    .subscribe(data => {
      const parsedData = JSON.parse(data);

      coords = parsedData;
      this.giveCoords(coords)
    })

    this.imprimirMensaje(routeFormResponse, coords)
  }

  imprimirMensaje(message: RouteFormResponse, coords: Coords[]) {
    console.log(JSON.stringify(message));
    console.log(coords)
  }

  giveCoords(coords: Coords[]){
    this.mapCommunication.sendRoute(coords)
  }
}
