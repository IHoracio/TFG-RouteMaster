import { Component, inject, input, output, ElementRef, Renderer2, AfterViewInit, signal, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TranslationService } from '../../../../services/singleton/translation.service';

@Component({
  selector: 'app-search-bar-filters',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-bar-filters.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './search-bar-filters.component.css'
})
export class SearchBarFiltersComponent implements AfterViewInit {
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

  isGasFiltersOpen = signal<boolean>(false);
  isSaveRouteOpen = signal<boolean>(false);

  translation = inject(TranslationService);
  private el = inject(ElementRef);
  private renderer = inject(Renderer2);

  ngAfterViewInit() {
    const saveRouteDropdown = this.el.nativeElement.querySelector('.save-route-dropdown');
    if (saveRouteDropdown) {
      const summary = saveRouteDropdown.querySelector('summary');
      if (summary) {
        this.renderer.listen(summary, 'click', (event: Event) => {
          if (!this.createdRoute()) {
            event.preventDefault();
            event.stopPropagation();
          }
        });
      }
    }
  }

  onRouteAliasChange(value: string) {
    this.routeAliasChange.emit(value);
  }

  toggleGasFilters() {
    this.isGasFiltersOpen.update(value => !value);
    if (this.isGasFiltersOpen()) {
      this.isSaveRouteOpen.set(false); // Cierra el otro de forma reactiva
    }
  }

  toggleSaveRoute() {
    if (this.createdRoute()) {
      this.isSaveRouteOpen.update(value => !value);
      if (this.isSaveRouteOpen()) {
        this.isGasFiltersOpen.set(false); // Cierra el otro de forma reactiva
      }
    }
  }

  public closeAllFilters() {
    this.isGasFiltersOpen.set(false);
    this.isSaveRouteOpen.set(false);
  }
}
