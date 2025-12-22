import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { Observable, timer, map } from 'rxjs';
import { WeatherRoute, WeatherData } from '../../../../Dto/weather-dtos';

@Component({
  selector: 'app-weather-overlay',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './weather-overlay.component.html',
  styleUrls: ['./weather-overlay.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WeatherOverlayComponent {
  private _data: WeatherRoute | null = null;

  @Output() close = new EventEmitter<void>();

  @Input()
  set data(value: WeatherRoute | null) {
    this._data = value ? this.normalizeWeatherRoute(value) : null;
  }
  get data(): WeatherRoute | null {
    return this._data;
  }

  public currentHour$: Observable<string> = timer(0, 60_000).pipe(
    map(() => String(new Date().getHours()))
  );

  private normalizeWeatherRoute(route: WeatherRoute): WeatherRoute {
    const copy: WeatherRoute = { ...route, wheatherData: [] };
    if (Array.isArray(route.wheatherData)) {
      copy.wheatherData = route.wheatherData.map(wd => ({
        ...wd,
        weatherDescription: this.toMap(wd.weatherDescription),
        temperatures: this.toMap(wd.temperatures)
      })) as WeatherData[];
    }
    return copy;
  }

  private toMap(maybe: any): Map<string, any> | null {
    if (!maybe) return null;
    if (maybe instanceof Map) return maybe;
    if (typeof maybe === 'object') return new Map(Object.entries(maybe));
    return null;
  }

  hourEntries(mapLike: Map<string, any> | null): Array<[string, any]> {
    if (!mapLike) return [];
    return Array.from(mapLike.entries());
  }

  getForHour(mapOrNull: Map<string, any> | null, hourStr: string | null): any | null {
    if (!mapOrNull || hourStr == null) return null;
    return mapOrNull.has(hourStr) ? mapOrNull.get(hourStr) : null;
  }

  hasForHour(mapOrNull: Map<string, any> | null, hourStr: string | null): boolean {
    if (!mapOrNull || hourStr == null) return false;
    return mapOrNull.has(hourStr);
  }

  trackByHour(_index: number, item: [string, any]): string {
    return item[0];
  }
}