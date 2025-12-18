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
      key: environment.googleMapsApiKey,
      v: 'weekly'
    });

    const mapsLib = (await importLibrary('maps')) as unknown as any;
    (await importLibrary('marker')) as unknown as any;

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

    const circleSvg = `
      <svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}" xmlns="http://www.w3.org/2000/svg">
        <circle cx="${cx}" cy="${cy}" r="${r}" fill="${color}" stroke="#ffffff" stroke-width="${stroke}"/>
      </svg>
    `;

    const parsed = new DOMParser().parseFromString(circleSvg, 'image/svg+xml').documentElement;
      parsed.style.position = 'absolute';
      parsed.style.left = '0';
      parsed.style.top = '0';
      parsed.style.transform = 'translate(-50%, -50%)';
      parsed.style.transformOrigin = 'center center';
    const advancedMarker = new google.maps.marker.AdvancedMarkerElement({
      map: this.map,
      position: {lat: c.lat, lng: c.lng},
      content: parsed,
    });
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
      <svg height="${size}" width="${size}" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
      viewBox="0 0 624.138 624.138" xml:space="preserve" fill="#e71616ff"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <g> <g> <g> <g> <path style="fill:#e71616ff;" d="M312.064,0C203.478,0,115.439,88.029,115.439,196.566c0,108.634,196.625,427.572,196.625,427.572 S508.698,305.2,508.698,196.566C508.698,88.029,420.698,0,312.064,0z M312.074,348.678 c-83.701,0-151.809-68.108-151.809-151.809c0-83.721,68.118-151.809,151.809-151.809c83.701,0,151.809,68.088,151.809,151.809 C463.883,280.57,395.775,348.678,312.074,348.678z"></path> <path style="fill:#010002;" d="M372.658,122.41l20.078,20.498v106.006c0,1.729-0.352,2.97-1.094,3.703 c-1.7,1.671-5.921,1.563-8.109,1.544c-4.719,0-8.275-0.938-9.956-2.609c-1.075-1.133-1.133-2.276-1.075-2.638V145.888H354.74 V95.2h-98.806v182.409H234.42v25.344h141.882v-25.344H354.74V156.01h7.591v92.475c-0.039,0.547-0.274,5.452,3.664,9.78 c3.722,3.996,9.565,6.028,17.42,6.028l1.514,0.02c3.742,0,9.682-0.449,13.805-4.484c2.745-2.697,4.133-6.36,4.133-10.933 V138.746l-22.989-23.448L372.658,122.41z M342.039,166.151h-70.922v-50.668h70.922V166.151z"></path> </g> </g> </g> </g> </g>
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