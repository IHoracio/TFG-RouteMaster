import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-user-info',
  imports: [CommonModule],
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent {
  user = signal({
    email: 'usuario@ejemplo.com',
    name: 'Juan',
    surname: 'Pérez'
  });

  favoriteRoutes = signal<any>([]);

  favoriteGasStations = signal<string[]>([]);

  shareRoute(route: any): void {

  }
}