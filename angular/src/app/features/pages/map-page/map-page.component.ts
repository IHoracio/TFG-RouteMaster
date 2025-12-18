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
  private gasStationsMarkers: any[] = [];

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
      key: environment.googleMapsMapId,
      v: 'weekly'
    });

    const mapsLib = (await importLibrary('maps')) as unknown as any;

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

    if (this.gasStationsMarkers && this.gasStationsMarkers.length) {
      this.gasStationsMarkers.forEach(marker => {
        marker.map = null;
      })
      this.gasStationsMarkers = [];
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

public markGasStations(gasStationsCoords: Coords[]): void {
  if(!this.map) {
    console.warn('Map not initialized');
    return;
  }

  if(!gasStationsCoords || gasStationsCoords.length === 0) return;

  const size = 36;
  const gasStationPointSvg = `
      <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24">
        <path d="M12 2C8 2 5 5 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-4-3-7-7-7z"
              fill="#EA4335" stroke="#ffffff" stroke-width="1.2"/>
        <circle cx="12" cy="9" r="2.3" fill="#ffffff"/>
      </svg>
    `;
  
  gasStationsCoords.forEach(c => {
      const container = document.createElement('div');
      container.innerHTML = gasStationPointSvg;
      container.style.width = '36px';
      container.style.height = '36px';
      container.style.display = 'block';
      container.style.transform = 'translateY(-6px)';

      const marker = new google.maps.marker.AdvancedMarkerElement({
        map: this.map,
        position: { lat: c.lat, lng: c.lng },
        content: container,
        title: (c as any).name || 'Gasolinera'
      });
      this.gasStationsMarkers.push(marker);
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