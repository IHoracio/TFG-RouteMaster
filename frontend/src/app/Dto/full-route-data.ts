import { GasStation } from "./gas-station";
import { Coords } from "./maps-dtos";
import { WeatherData } from "./weather-dtos";


export interface FullRouteData {
  totalDistance: string;
  totalDuration: string;
  polylineCoords: Coords[];
  legCoords: Coords[];
  gasStations: GasStation[];
  weatherData: WeatherData[];
}