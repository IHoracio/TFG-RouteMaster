import { FavouriteGasStation } from "./gas-station";

export interface User {
    email: string,
    password: string,
    passwordConfirmation: string,
    name: string,
    surname: string
}
export interface UserLoginDTO{
  user: string,
  password: string
}
export interface UserDto {
  id: number;
  email: string;
  password: string;
  passwordConfirmation: string;
  name: string;
  surname: string;
  userPreferences: UserPreferencesDto;
  savedRoutes: SavedRouteDto[];
  favouriteGasStations: FavouriteGasStation[];
  gasStationPriority: GasStationPriority;
  routePreferences: RoutePreferencesDto;
}
export interface UserPreferencesDto {
  theme: string;
  language: string;
  user: string;
}
export interface SavedRouteDto {
  routeId: number;
  name: string;
  points: RoutePointDto[];
  user: string;
  optimizeWaypoints: boolean;
  optimizeRoute: boolean;
  avoidTolls: boolean;
  language: string;
}
export interface RoutePointDto {
  id: number;
  type: RoutePointType;
  address: string;
  savedRoute: string;
}
export type RoutePointType = 'ORIGIN' | 'DESTINATION' | 'WAYPOINT';

export interface RoutePreferencesDto {
  preferredBrands: string[];
  radioKm: number;
  fuelType: string;
  maxPrice: number;
  mapView: MapView;
  avoidTolls: boolean;
}

export type MapView = 'SATELLITE' | 'SCHEMATIC';
export type GasStationPriority = 'PRICE' | 'DISTANCE' | 'RATING';

