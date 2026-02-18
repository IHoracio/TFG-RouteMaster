import { Component, inject, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { TranslationService } from '../../../../services/translation.service';

@Component({
  selector: 'app-search-bar-filters',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './search-bar-filters.component.html',
  styleUrl: './search-bar-filters.component.css'
})
export class SearchBarFiltersComponent {
  isLoggedIn = input<boolean>(false);
  filterByCheapest = input<boolean>(false);
  filterByBrands = input<boolean>(false);
  filterByMaxPrice = input<boolean>(false);
  createdRoute = input<boolean>(false);
  routeAlias = input<string>('');
  successfulMessage = input<string>('');
  errorMessage = input<string>('');
  showShareMessage = input<boolean>(false);

  toggleFilterByCheapest = output<void>();
  toggleFilterByBrands = output<void>();
  toggleFilterByMaxPrice = output<void>();
  saveRoute = output<void>();
  shareRoute = output<void>();
  routeAliasChange = output<string>();

  translation = inject(TranslationService);

  onRouteAliasChange(value: string) {
    this.routeAliasChange.emit(value);
  }
}
