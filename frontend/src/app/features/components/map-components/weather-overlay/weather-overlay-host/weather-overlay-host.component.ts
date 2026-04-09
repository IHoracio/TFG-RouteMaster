import {
  Component,
  input,
  output
} from '@angular/core';
import { WeatherOverlayComponent } from '../weather-overlay.component';
import { WeatherData } from '../../../../../Dto/weather-dtos';

@Component({
  selector: 'app-weather-overlay-host',
  standalone: true,
  imports: [WeatherOverlayComponent],
  templateUrl: './weather-overlay-host.component.html'
})
export class WeatherOverlayHostComponent {
  weatherRoute = input<WeatherData[] | null>(null);
  visibleChange = output<void>();
}
