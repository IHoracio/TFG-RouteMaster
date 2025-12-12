import {
  Component,
  OnDestroy
} from '@angular/core';
import { setOptions, importLibrary } from '@googlemaps/js-api-loader';
import { Coords } from './Utils/google-route.mapper';
import { MapCommunicationService } from '../../../services/map/map-communication.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-map-page',
  templateUrl: './map-page.component.html',
  styleUrls: ['./map-page.component.css']
})
export class MapPageComponent {

  private map?: google.maps.Map;
  private routePolyline?: google.maps.Polyline;
  private startMarker?: any;
  private endMarker?: any;
  private AdvancedMarkerViewCtor?: any; // con esto se "pintan" las paradas

  constructor(private mapComm: MapCommunicationService) {}

  async ngOnInit(): Promise<void> {
    await this.initMap();
  }

  ngOnDestroy(): void {
    this.clearRoute();
    this.AdvancedMarkerViewCtor = undefined;
    (this.map as any) = undefined;
  }

  private async initMap(): Promise<void> {
    this.mapComm.registerMapPage(this);

    setOptions({
      key: environment.googleMapsApiKey,
      v: 'weekly'
    });

    const mapsLib = (await importLibrary('maps')) as unknown as google.maps.MapsLibrary;
    const markerLib = (await importLibrary('marker')) as unknown as any;

    const { Map } = mapsLib;
    this.AdvancedMarkerViewCtor = markerLib?.AdvancedMarkerView ?? markerLib?.AdvancedMarkerView ?? undefined;

    const mapOptions: google.maps.MapOptions = {
      center: { lat: 40.4168, lng: -3.7038 },
      zoom: 6,
      mapTypeControl: true
    };

    this.map = new Map(
      document.getElementById('map') as HTMLElement,
      mapOptions
    );
  }

  public drawRoute(coords: Coords[]): void {

    if (!this.map) {
      console.warn('Map not initialiced');
      return;
    }

    const path: google.maps.LatLngLiteral[] = coords;

    if (!this.routePolyline) {
      this.routePolyline = new google.maps.Polyline({
        path,
        geodesic: true,
        strokeColor: '#4285F4',
        strokeOpacity: 0.85,
        strokeWeight: 5,
        map: this.map
      });
    } else {
      this.routePolyline.setPath(path);
    }

    const bounds = new google.maps.LatLngBounds();
    path.forEach(p => bounds.extend(p));
    this.map.fitBounds(bounds, { top: 50, right: 50, bottom: 50, left: 50 });
  }

  public clearRoute(): void {
    if (this.routePolyline) {
      this.routePolyline.setMap(null);
      this.routePolyline = undefined;
    }
    if (this.startMarker) {
      this.startMarker.map = null;
      this.startMarker = undefined;
    }
    if (this.endMarker) {
      this.endMarker.map = null;
      this.endMarker = undefined;
    }
  }

}