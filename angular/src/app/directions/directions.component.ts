import { Component, Input } from '@angular/core';
import { DirectionsInfo } from './directions-info';


@Component({
  selector: 'app-directions',
  standalone: false,
  templateUrl: './directions.component.html',
  styleUrl: './directions.component.css'
})
export class DirectionsComponent {
  @Input() directions: DirectionsInfo = { destination: "" };
}