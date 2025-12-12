import {
  Component,
  ViewChild,
  ElementRef
} from '@angular/core';
import { setOptions, importLibrary } from '@googlemaps/js-api-loader';
import { Coords } from './Utils/google-route.mapper';
import { MapCommunicationService } from '../../../services/map/map-communication.service';
import { environment } from '../../../../environments/environment';


@Component({
  selector: 'app-map-page',
  imports: [],
  templateUrl: './map-page.component.html',
  styleUrl: './map-page.component.css'
})
export class MapPageComponent {

  constructor(private mapComm: MapCommunicationService) {}
  

  async ngOnInit(): Promise<void> {
    await this.initMap();
  }

  private async initMap(): Promise<void> {
    this.mapComm.registerMapPage(this);

    setOptions({
      key: environment.googleMapsApiKey,
      v: 'weekly'
    });

    const { Map } = (await importLibrary('maps')) as unknown as google.maps.MapsLibrary;

    const mapOptions: google.maps.MapOptions = {
      center: { lat: 40.4168, lng: -3.7038 },
      zoom: 6
    };

    const map = new Map(
      document.getElementById('map') as HTMLElement,
      mapOptions
    );
  }

  public drawRoute(coords: Coords[]){
    console.log("Me han llegado !!!!!");
    console.log(coords)
  }
}
