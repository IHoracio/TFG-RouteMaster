import { Coords, RouteGroupResponse } from "../../../../Dto/maps-dtos";

export function extractAllCoords(routeGroupResponse: RouteGroupResponse): Coords[] {
  const coordsList: Coords[] = [];
  if (routeGroupResponse == null || !Array.isArray(routeGroupResponse.routes)) return coordsList;

  for (const route of routeGroupResponse.routes) {
    if (route.legs == null || !Array.isArray(route.legs)) continue;

    for (const leg of route.legs) {
      if (leg.steps != null && Array.isArray(leg.steps)) {
        for (const step of leg.steps) {
          if (isValidCoord(step.start_location)) coordsList.push(step.start_location);

        }
  
        if (isValidCoord(leg.end_location)) coordsList.push(leg.end_location);
      } 
    }
  }

  return coordsList;
}

function isValidCoord(coords: any): coords is Coords {

  return coords != null && 
  typeof coords.lat === 'number' && 
  typeof coords.lng === 'number';
}
