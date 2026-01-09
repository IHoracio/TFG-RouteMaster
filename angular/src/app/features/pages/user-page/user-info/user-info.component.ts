import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-info',
  imports: [CommonModule],
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent {
  private router = inject(Router);

  user = signal({
    email: 'usuario@ejemplo.com',
    name: 'Juan',
    surname: 'Pérez'
  });

  favoriteRoutes = signal<any>([]);

  favoriteGasStations = signal<string[]>([]);

  shareRoute(route: any): void {

  }

  goToLanding(): void {
    this.router.navigate(['/']);
  }

  goToPreferences(): void {
    const element = document.getElementById('add-gas-stations');
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }

}