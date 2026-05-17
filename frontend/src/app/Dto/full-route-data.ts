import { GasStation } from "./gas-station";
import { Coords } from "./maps-dtos";
import { PlaceSelection } from "./place-selection";
import { WeatherData } from "./weather-dtos";


export interface FullRouteData {
  totalDistance: string;
  totalDuration: string;
  pointDTO: PointDTO[];
  polylineCoords: Coords[];
  legCoords: Coords[];
  gasStations: GasStation[];
  weatherData: WeatherData[];
}

export interface PointDTO {
  type: 'ORIGIN' | 'WAYPOINT' | 'DESTINATION';
  placeSelection: PlaceSelection;
}