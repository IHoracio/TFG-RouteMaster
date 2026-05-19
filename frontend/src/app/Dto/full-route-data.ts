import { GasStation } from "./gas-station";
import { Coords } from "./maps-dtos";
import { PlaceSelection } from "./place-selection";
import { WeatherData } from "./weather-dtos";


export interface FullRouteData {
  totalDistance: string;
  totalDuration: string;
  puntosDTO: PuntosDTO[];
  polylineCoords: Coords[];
  legCoords: Coords[];
  gasStations: GasStation[];
  weatherData: WeatherData[];
}

export interface PuntosDTO {
  type: 'ORIGIN' | 'WAYPOINT' | 'DESTINATION';
  placeSelection: PlaceSelection;
}