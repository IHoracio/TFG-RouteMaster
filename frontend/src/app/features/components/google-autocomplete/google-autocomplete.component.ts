import { Component, ElementRef, EventEmitter, Input, Output, ViewChild, AfterViewInit, inject, effect, untracked } from '@angular/core';
import { GoogleMapsLoaderService } from '../../../services/google.maps.loader.service';
import { TranslationService } from '../../../services/translation.service';
import { PlaceSelection } from '../../../Dto/place-selection';

// --- INTERCEPTOR DE SHADOW DOM ---
// Esto debe estar fuera de la clase para que se ejecute una sola vez a nivel global
const originalAttachShadow = Element.prototype.attachShadow;
Element.prototype.attachShadow = function (init: ShadowRootInit) {
  if (this.localName === "gmp-place-autocomplete") {
    // Forzamos modo open para poder meter estilos
    const shadow = originalAttachShadow.call(this, { ...init, mode: "open" });
    const style = document.createElement("style");

    // Inyectamos los estilos para saltarnos la restriccion de google constructed stylesheet
    style.textContent = `
      /* Contenedores base */
      .widget-container { border: none !important; background: transparent !important; }
      .input-container { padding: 0px !important; background-color: var(--surface-secondary) !important; border-radius: 8px !important; }
      .focus-ring { display: none !important; }
      
      /* Lista desplegable */
      .dropdown {
        background-color: var(--surface-color) !important;
        border: 1px solid var(--border-color) !important;
        border-radius: 0 0 8px 8px !important;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15) !important;
      }

      /* Filas de predicción con el margen solicitado */
      .place-autocomplete-element-row {
        margin-left: 12px !important;
        display: flex;
        align-items: center;
      }

      /* Textos de predicción */
      .place-autocomplete-element-text-div,
      .place-autocomplete-element-place-name,
      .place-autocomplete-element-place-details {
        color: var(--text-color) !important;
      }

      /* Texto que coincide (Matched) */
      .place-autocomplete-element-place-result--matched {
        color: var(--primary-color) !important;
        font-weight: bold !important;
      }

      /* ICONOS Y BOTONES */
      .clear-button svg path, 
      .back-button svg path,
      .info-icon path,
      .place-autocomplete-element-prediction-item-icon path,
      .autocomplete-icon svg path {
        fill: var(--text-color) !important;
      }

      /* ICONOS Y BOTONES HOVER*/
      .input-container button.clear-button:hover {
        background-color: var(--hover-button) !important;
      }

      /* LOGO DE GOOGLE (Atribuciones) */
      /* Forzamos tanto el fill como la variable interna de Google */
      .attribution__logo--default path, 
      .attribution__logo--default polygon {
        fill: var(--text-color) !important;
      }

      .container {
        --gmp-internal-attribution-color: var(--text-color) !important;
      }

      /* Elementos de la lista */
      li {
        cursor: pointer !important;
        transition: all 0.2s ease !important;
        padding: 0px !important;
      }

      li:hover {
        background-color: var(--hover-button) !important;
        transform: translateX(4px) scale(1.01);
      }

      /* Input principal */
      input {
        color: var(--text-color) !important;
        background-color: transparent !important;
        border: none !important;
      }
    `;

    shadow.appendChild(style);
    return shadow;
  }
  return originalAttachShadow.call(this, init);
};

@Component({
  selector: 'app-google-autocomplete',
  standalone: true,
  templateUrl: './google-autocomplete.component.html',
  styleUrls: ['./google-autocomplete.component.css']
})
export class GoogleAutocompleteComponent implements AfterViewInit {
  @ViewChild('container') container!: ElementRef<HTMLDivElement>;

  @Input() placeholder: string = '';
  @Input() types: string[] = ['address'];
  @Input() country: string | string[] = '';

  @Output() onPlaceSelected = new EventEmitter<PlaceSelection>();

  private mapsLoader = inject(GoogleMapsLoaderService);
  private translation = inject(TranslationService);
  private isInitialized = false;

  constructor() {
    effect(() => {
      this.translation.getCurrentLang();
      const p = this.placeholder;

      if (this.isInitialized) {
        untracked(() => {
          const autocompleteEl = this.container.nativeElement.querySelector('gmp-place-autocomplete') as any;
          const internalInput = autocompleteEl?.shadowRoot?.querySelector('input');
          const currentText = autocompleteEl?.value || internalInput?.value || '';

          if (!currentText || currentText.trim() === '') {
            this.initAutocomplete();
          } else {
            if (internalInput) {
              internalInput.placeholder = this.placeholder;
              internalInput.setAttribute('placeholder', this.placeholder);
            }
          }
        });
      }
    });
  }

  async ngAfterViewInit() {
    this.isInitialized = true;
    await this.initAutocomplete();
  }

  private async initAutocomplete() {
    try {
      const placesLib = await this.mapsLoader.getPlacesLibrary() as any;
      const { PlaceAutocompleteElement } = placesLib;

      const options = {
        includedPrimaryTypes: ['geocode'],
        includedRegionCodes: ['es'],
      };

      const autocomplete = new PlaceAutocompleteElement(options);

      autocomplete.setAttribute('placeholder', this.placeholder);
      autocomplete.setAttribute('lang', this.translation.getCurrentLang() || 'es');

      this.container.nativeElement.innerHTML = '';
      this.container.nativeElement.appendChild(autocomplete as any);

      // Aplicar placeholder con delay tras inserción
      setTimeout(() => {
        const input = autocomplete.shadowRoot?.querySelector('input');
        if (input) {
          input.placeholder = this.placeholder || 'Buscar ubicación...';
        }
      }, 100);

      autocomplete.addEventListener('gmp-placeselect', async (event: any) => {
        const place = event.place;
        if (!place) return;
        await place.fetchFields({ fields: ['id', 'formattedAddress', 'displayName'] });
        this.onPlaceSelected.emit({
          placeId: place.id || '',
          address: place.formattedAddress || '',
          name: place.displayName || '',
        });
      });

    } catch (error) {
      console.error('Error en GoogleAutocompleteComponent:', error);
    }
  }
}