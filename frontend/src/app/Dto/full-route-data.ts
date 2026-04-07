import { GasStation } from "./gas-station";
import { Coords } from "./maps-dtos";
import { WeatherData } from "./weather-dtos";


export interface FullRouteData {
  totalDistance: String;
  totalDuration: String;
  polylineCoords: Coords[];
  legCoords: Coords[];
  gasStations: GasStation[];
  weatherData: WeatherData[];
}