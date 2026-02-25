import { Component, inject, input, output, ElementRef, Renderer2, AfterViewInit } from '@angular/core';
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
}
