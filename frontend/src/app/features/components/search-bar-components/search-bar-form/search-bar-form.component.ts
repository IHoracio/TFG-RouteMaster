import { Component, inject, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { RouteFormResponse } from '../../../../Dto/route-form-response';
import { FavouriteGasStation } from '../../../../Dto/gas-station';
import { SavedRouteDto } from '../../../../Dto/user-dtos';
import { TranslationService } from '../../../../services/translation.service';

@Component({
  selector: 'app-search-bar-form',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf],
  templateUrl: './search-bar-form.component.html',
  styleUrl: './search-bar-form.component.css'
})
export class SearchBarFormComponent {
  activeTab = input<string>('destination');
  isFormCollapsed = input<boolean>(false);
  routeFormResponse = input<RouteFormResponse>({
    origin: '',
    destination: '',
    waypoints: [],
    optimizeWaypoints: false,
    optimizeRoute: false,
    avoidTolls: false
  });
  waypointTypes = input<string[]>([]);
  favouriteGasStations = input<FavouriteGasStation[]>([]);
  savedRoute = input<SavedRouteDto[]>([]);
  selectedSavedRoute = input<number | null>(null);
  isLoggedIn = input<boolean>(false);
  successfulMessage = input<string>('');
  errorMessage = input<string>('');

  submit = output<void>();
  toggleFormCollapse = output<void>();
  addWaypoint = output<void>();
  deleteWaypoint = output<void>();
  selectedSavedRouteChange = output<number | null>();
  originFocus = output<void>();

  translation = inject(TranslationService);

  trackByIndex(index: number) {
    return index;
  }
}
