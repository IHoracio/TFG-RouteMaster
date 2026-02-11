import { Injectable, signal } from '@angular/core';
import { GasStation } from '../../../Dto/gas-station';

@Injectable({
  providedIn: 'root'
})
export class GasStationSelectionService {
  selectedStation = signal<GasStation | null>(null);
}