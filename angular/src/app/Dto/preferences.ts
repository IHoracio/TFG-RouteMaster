export interface Preferences {
    code: string,
    label: string
}

export interface DefaultUserPreferences {
    preferredBrands: string[],
    radioKm: number,
    fuelType: string,
    emissionType: string,
    maxPrice: number,
    mapView: string,
    theme: string,
    language: string
}