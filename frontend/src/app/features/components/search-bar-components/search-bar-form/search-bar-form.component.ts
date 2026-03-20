import { Component, inject, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { RouteFormResponse } from '../../../../Dto/route-form-response';
import { FavouriteGasStation } from '../../../../Dto/gas-station';
import { SavedRouteDto } from '../../../../Dto/user-dtos';
import { TranslationService } from '../../../../services/translation.service';
import { GoogleAutocompleteComponent } from '../../google-autocomplete/google-autocomplete.component';
import { PlaceSelection } from '../../../../Dto/place-selection';

@Component({
  selector: 'app-search-bar-form',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, GoogleAutocompleteComponent],
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
  selectedSavedRoute = input<string | null>(null);
  isLoggedIn = input<boolean>(false);
  successfulMessage = input<string>('');
  errorMessage = input<string>('');

  searchSubmit = output<void>();
  toggleFormCollapse = output<void>();
  addWaypoint = output<void>();
  deleteWaypoint = output<void>();
  selectedSavedRouteChange = output<string | null>();
  originFocus = output<void>();
  originSelected = output<PlaceSelection>();
  destinationSelected = output<PlaceSelection>();
  waypointSelected = output<{ index: number; selection: PlaceSelection }>();

  translation = inject(TranslationService);

  trackByIndex(index: number) {
    return index;
  }
}
