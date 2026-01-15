import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GasStation } from '../../../Dto/gas-station';

@Injectable({
  providedIn: 'root'
})
export class GasStationService {
  private apiUrl = 'http://localhost:8080/api/oil/gasolineras/radio/address';

  constructor(private http: HttpClient) {}

  searchGasStations(address: string, radio: number): Observable<GasStation[]> {
    const formattedAddress = address.replace(/\s+/g, '_');
    const url = `${this.apiUrl}?direccion=${encodeURIComponent(formattedAddress)}&radio=${radio}`;
    return this.http.get<GasStation[]>(url);
  }
}