import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserInfoService } from '../../../../services/user-page/user-info.service';
import { GasStation } from '../../../../Dto/gas-station';

@Component({
  selector: 'app-user-info',
  imports: [CommonModule],
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent implements OnInit {
  private router = inject(Router);
  private userInfoService = inject(UserInfoService);

  user = signal<any>({});

  favoriteRoutes = signal<any[]>([]);

  favoriteGasStations = signal<GasStation[]>([]);

  private userId: string = "1";
  private mail: string = 'prueba@gmail.com';

  ngOnInit(): void {
    console.log('UserInfoComponent ngOnInit called');
    this.userInfoService.getUserInfo(this.userId, this.mail).subscribe({
      next: (data) => {
        console.log('UserInfo data received:', data);
        this.user.set({
          email: data.email,
          name: data.name,
          surname: data.surname
        });
        this.favoriteRoutes.set(data.favoriteRoutes || []);
        this.favoriteGasStations.set(data.favoriteGasStations || []);
      },
      error: (error) => {
        console.error('Error fetching user info:', error);
      }
    });
  }

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