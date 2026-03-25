import { PlaceSelection } from "./place-selection";

export interface RouteFormResponse {
    origin: PlaceSelection | null;
    destination: PlaceSelection | null;
    waypoints: PlaceSelection[];
    optimizeWaypoints: boolean,
    optimizeRoute: boolean,
    avoidTolls: boolean,
    radioKm?: number;
}
