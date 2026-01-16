import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserPreferencesService } from '../../../../services/user-page/user-preferences.service';
import { FavouriteGasStation, GasStation } from '../../../../Dto/gas-station';
import { UserInfoService } from '../../../../services/user-page/user-info.service';
import { forkJoin } from 'rxjs';
import { GasStationService } from '../../../../services/user-page/gas-station/gas-station.service';

@Component({
  selector: 'app-user-info',
  imports: [CommonModule],
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent implements OnInit {
  private router = inject(Router);
  private userInfoService = inject(UserInfoService);
  private userPreferencesService = inject(UserPreferencesService);
  private gasStationService = inject(GasStationService);

  favoriteRoutes = this.userInfoService.getRoutesSignal();

  user = this.userInfoService.getUserSignal();
  favoriteGasStations = this.userPreferencesService.getFavoriteGasStationsSignal();

  ngOnInit(): void {
    this.userInfoService.getUserInfo().subscribe({
      next: (data) => {
        this.user.set({
          email: data.email || 'N/A',
          name: data.name || 'N/A',
          surname: data.surname || 'N/A'
        });
      },
      error: (error) => {
        console.error('Error fetching user info:', error);
        this.user.set({ email: 'Error', name: 'Error', surname: 'Error' });
      }
    });

    this.userInfoService.getUserRoutes().subscribe({
      next: (data: any[]) => {
        this.favoriteRoutes.set(data || []);
        if (data && data.length > 0) {
          const executeCalls = data.map(route => this.userInfoService.executeRoute(route.routeId));
          forkJoin(executeCalls).subscribe({
            next: (results) => {
              this.favoriteRoutes.update(routes => routes.map((route, index) => {
                const res = results[index];
                const distanceKm = (res.distanceMeters / 1000).toFixed(2);
                const hours = Math.floor(res.durationSeconds / 3600);
                const minutes = Math.floor((res.durationSeconds % 3600) / 60);
                const durationFormatted = `${hours}h ${minutes}m`;
                return { ...route, distanceKm, durationFormatted };
              }));
            },
            error: (err) => {
              console.error('Error executing routes:', err);
            }
          });
        }
      },
      error: (error) => {
        console.error('Error fetching user routes:', error);
      }
    });

    this.userPreferencesService.getUserFavouriteGasStations().subscribe({
      next: (data: FavouriteGasStation[]) => {
        this.favoriteGasStations.set(data || []);
        this.favoriteGasStations().forEach(favorite => {
          if (!favorite.latitud || !favorite.longitud) {
            this.gasStationService.getGasStation(favorite.idEstacion).subscribe({
              next: (fullStation: GasStation) => {
                this.favoriteGasStations.update(stations =>
                  stations.map(s => s.idEstacion === favorite.idEstacion ? { ...fullStation, alias: favorite.alias } as FavouriteGasStation : s)
                );
              },
              error: (err) => {
                console.error('Error obteniendo datos completos de gasolinera favorita:', err);
              }
            });
          }
        });
      },
      error: (error) => {
        console.error('Error fetching favorite gas stations:', error);
      }
    });
  }

  selectStation(station: FavouriteGasStation): void {
    this.router.navigate(['/user-preferences'], { state: { selectedStation: station } });
  }

  shareRoute(route: any): void {

  }

  deleteRoute(route: any): void {
    if (confirm(`¿Estás seguro de que quieres eliminar la ruta "${route.name}"?`)) {
      this.userInfoService.deleteRoute(route.routeId).subscribe({
        next: () => {
          this.favoriteRoutes.update(routes => routes.filter(r => r.routeId !== route.routeId));
        },
        error: (err) => {
          console.error('Error eliminando ruta:', err);
          alert('Error al eliminar la ruta. Inténtalo de nuevo.');
        }
      });
    }
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