export interface WeatherRoute {
    wheatherData: WeatherData[];
}

export interface WeatherData {
  address: string;
  weatherDescription: Map<string, string>;
  temperatures: Map<string, number>;
}