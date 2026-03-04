export interface WeatherData {
  address: string,
  weatherDescription: { [key: string]: string },
  temperatures: { [key: string]: number },
  feelsLike: { [key: string]: number },
  windSpeed: { [key: string]: number },
  visibility: { [key: string]: number }
  alerts: Alert[];
}

export interface Alert {
  description: string,
  event: string,
  sender_name: string,
  start: number,
  end: number,
}