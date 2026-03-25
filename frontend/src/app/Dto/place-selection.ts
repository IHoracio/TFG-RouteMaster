export interface PlaceSelection {
  /** ID único de Google Maps para el lugar. 
   * Es lo que debes enviar al Backend para usar en la Directions API. */
  placeId: string;

  /** Dirección completa y formateada (ej: "Calle Mayor 1, Madrid, España").
   * Útil para mostrar en etiquetas o resúmenes de usuario. */
  address: string;

  /** Nombre del lugar o establecimiento (ej: "Gasolinera Repsol" o "Restaurante El Prado").
   * Opcional, ya que no todas las direcciones postales tienen un nombre asociado. */
  name?: string;

  /** Coordenadas geográficas exactas del lugar.
   * Útil si necesitas centrar el mapa o pintar un marcador inmediatamente. */
  coords?: { 
    lat: number; 
    lng: number; 
  };
}