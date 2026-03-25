import { Component, ElementRef, EventEmitter, Input, Output, ViewChild, AfterViewInit, inject, effect, untracked } from '@angular/core';
import { GoogleMapsLoaderService } from '../../../services/google.maps.loader.service';
import { TranslationService } from '../../../services/translation.service';
import { PlaceSelection } from '../../../Dto/place-selection';

// =============================================================================
// INTERCEPTOR GLOBAL DE SHADOW DOM
// =============================================================================
// Este código se ejecuta UNA SOLA VEZ al cargar el módulo.
// Fuerza que el Shadow DOM de <gmp-place-autocomplete> sea "open" en lugar de "closed",
// permitiendo inyectar estilos CSS directamente dentro de él.
// Esto es necesario porque Google usa constructed stylesheets con prioridad muy alta.
const originalAttachShadow = Element.prototype.attachShadow;
Element.prototype.attachShadow = function (init: ShadowRootInit) {
  if (this.localName === "gmp-place-autocomplete") {
    // Creamos el shadow root en modo open
    const shadow = originalAttachShadow.call(this, { ...init, mode: "open" });

    // Creamos una hoja de estilos interna
    const style = document.createElement("style");

    // Inyectamos todos los estilos personalizados que queremos forzar
    // dentro del Shadow DOM de Google (esto sobreescribe muchas restricciones)
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

      .container {
        --gmp-internal-attribution-color: var(--text-color) !important;
      }

      /* Elementos de la lista */
      li {
        cursor: pointer !important;
        transition: all 0.2s ease !important;
        padding: 0px !important;
      }

      li:hover,
      li:focus,
      li:focus-visible {
        background-color: var(--hover-button) !important;
        transform: translateX(4px) scale(1.01);
      }

      button:hover,
      button:focus,
      button:focus-visible {
        background-color: var(--hover-button) !important;
        transform: translateX(4px) scale(1.01);
      }

      .dropdown > ul > li[aria-selected="true"] {
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

    // Paso 4: Añadimos los estilos al shadow root
    shadow.appendChild(style);

    return shadow;
  }
  // Si no es nuestro componente → comportamiento original
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
    // Efecto reactivo para cambios de idioma o placeholder
    effect(() => {
      const lang = this.translation.getCurrentLang();
      const currentPlaceholder = this.placeholder;

      if (this.isInitialized) {
        untracked(() => {
          const autocompleteEl = this.container.nativeElement.querySelector('gmp-place-autocomplete') as any;

          if (autocompleteEl) {
            this.initAutocomplete();
          }
        });
      }
    });
  }

  async ngAfterViewInit() {
    this.isInitialized = true;
    await this.initAutocomplete();
  }

  /**
   * Inicializa o reinicializa el widget de Google Autocomplete.
   * Este método es el corazón del componente y se llama:
   * - Al montar el componente (ngAfterViewInit)
   * - Cuando cambia el idioma (effect)
   */
  private async initAutocomplete() {

    try {
      // Cargamos la librería de Places de Google
      const placesLib = await this.mapsLoader.getPlacesLibrary() as any;
      const { PlaceAutocompleteElement } = placesLib;

      // Normalizamos los códigos de región (evitamos errores de Google)
      let regionCodes: string[] = [];
      if (Array.isArray(this.country)) {
        regionCodes = this.country
          .map(code => (code || '').trim().toLowerCase())
          .filter(code => code.length === 2 && /^[a-z]{2}$/.test(code));
      } else if (typeof this.country === 'string') {
        const trimmed = this.country.trim().toLowerCase();
        if (trimmed.length === 2 && /^[a-z]{2}$/.test(trimmed)) {
          regionCodes = [trimmed];
        }
      }
      if (regionCodes.length === 0) {
        regionCodes = ['es'];
      }

      // Creamos la instancia del widget
      const autocomplete = new PlaceAutocompleteElement({
        includedRegionCodes: regionCodes,
      });

      // Configuramos idioma y placeholder
      autocomplete.setAttribute('lang', this.translation.getCurrentLang());
      autocomplete.setAttribute('placeholder', this.placeholder);

      // Limpiamos el contenedor e insertamos el widget
      const containerEl = this.container.nativeElement;
      containerEl.innerHTML = '';
      containerEl.appendChild(autocomplete);

      // Añadimos el listener del evento de selección
      autocomplete.addEventListener('gmp-select', async (event: any) => {

        const { placePrediction } = event;
        if (!placePrediction) {
          console.warn('[Autocomplete] No se encontró placePrediction en el evento');
          return;
        }

        try {
          // Convertimos la predicción en un objeto Place completo
          const place = placePrediction.toPlace();

          // Pedimos los campos adicionales que necesitamos
          await place.fetchFields({
            fields: ['id', 'formattedAddress', 'displayName']
          });

          // Emitimos el evento hacia el componente padre
          this.onPlaceSelected.emit({
            placeId: place.id || '',
            address: place.formattedAddress || '',
            name: place.displayName || '',
          });
        } catch (err) {
          console.error('[Autocomplete] Error al procesar selección:', err);
        }
      });
    } catch (error) {
      console.error('[Autocomplete] Error crítico en initAutocomplete:', error);
    }
  }
}