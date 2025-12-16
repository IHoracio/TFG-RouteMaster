import {
  Component,
  OnDestroy
} from '@angular/core';
import { setOptions, importLibrary } from '@googlemaps/js-api-loader';

import { MapCommunicationService } from '../../../services/map/map-communication.service';
import { environment } from '../../../../environments/environment';
import { Coords } from '../../../Dto/maps-dtos';

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
  private waypoints: any[] = [];

  constructor(private mapComm: MapCommunicationService) {}

  async ngOnInit(): Promise<void> {
    await this.initMap();
  }

  ngOnDestroy(): void {
    this.clearRoute();
    (this.map as any) = undefined;
    this.mapComm.unregisterMapPage(this);
  }

  private async initMap(): Promise<void> {
    setOptions({
      key: environment.googleMapsApiKey,
      v: 'weekly'
    });

    const mapsLib = (await importLibrary('maps')) as unknown as any;
    const markerLib = (await importLibrary('marker')) as unknown as any;

    const { Map } = mapsLib;

    const mapOptions: google.maps.MapOptions = {
      center: { lat: 40.4168, lng: -3.7038 },
      zoom: 6,
      tilt: 45,
      mapTypeControl: true,
      fullscreenControl: true,
      disableDefaultUI: true,
      streetViewControl: false,
      zoomControl: false,
      mapId: environment.googleMapsMapId
    };

    this.map = new Map(
      document.getElementById('map') as HTMLElement,
      mapOptions
    );

    this.mapComm.registerMapPage(this);
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

    if (this.waypoints && this.waypoints.length) {
      this.waypoints.forEach(wp => {
        wp.map = null; 
      });
      this.waypoints = [];
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

public drawPoints(coords: Coords[]): void {
  if (!this.map) {
    console.warn('Map not initialiced');
    return;
  }
  if (!coords || coords.length === 0) return;

  coords.forEach((c, idx) => {
    const isStart = idx === 0;
    const isEnd = idx === coords.length - 1;
    
    const color = isStart || isEnd ? '#200f9dff' : '#4285F4';
    const size = 18;
    const stroke = Math.max(1, Math.round(size * 0.12));
    const cx = size / 2;
    const cy = size / 2;
    const r = Math.max(1, Math.round((size / 2) - stroke - 1));

    const circle = `
      <svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}" xmlns="http://www.w3.org/2000/svg">
        <circle cx="${cx}" cy="${cy}" r="${r}" fill="${color}" stroke="#ffffff" stroke-width="${stroke}"/>
      </svg>
    `;

    const advancedMarker = new google.maps.marker.AdvancedMarkerElement({
      map: this.map,
      position: {lat: c.lat, lng: c.lng},
      content: new DOMParser().parseFromString(circle, 'image/svg+xml').documentElement,
    })

    this.waypoints.push(advancedMarker);

    if (isStart) this.startMarker = advancedMarker;
    if (isEnd) this.endMarker = advancedMarker;
  });
}

}

/* 
MARKER DEPRECADO

  public drawPoints(coords: Coords[]): void {
    if (!this.map) {
      console.warn('Map not initialiced');
      return;
    }

    if (!coords || coords.length === 0) {
      return;
    }

    coords.forEach((c, idx) => {
      const isStart = idx === 0;
      const isEnd = idx === coords.length - 1;
      const color = isStart || isEnd ? '#200f9dff' : '#4285F4';
      const scale = 5;

      const icon: google.maps.Symbol = {
        path: google.maps.SymbolPath.CIRCLE,
        fillColor: color,
        fillOpacity: 1,
        strokeColor: '#ffffff',
        strokeWeight: 2,
        scale
      };

      const marker = new google.maps.Marker({
        position: c,
        map: this.map,
        icon
      });

      this.waypoints.push(marker);

      if (isStart) {
        this.startMarker = marker;
      }
      if (isEnd) {
        this.endMarker = marker;
      }
    });
  }


}

*/