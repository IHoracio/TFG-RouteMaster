import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth/auth-service.service';
import { Router } from '@angular/router';
import { UserPreferencesService } from '../../services/user-page/user-preferences.service';
import { UserInfoService } from '../../services/user-page/user-info.service';

@Component({
  selector: 'app-logout',
  imports: [],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.css'
})
export class LogoutComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private userPrefsService = inject(UserPreferencesService);
  private userInfoService = inject(UserInfoService);

  ngOnInit() {
    // 1. Llamada al servidor para invalidar sesión (Cookie/Token)
    this.authService.logout().subscribe({
      next: () => this.performFullCleanup(),
      error: () => this.performFullCleanup()
    });
  }

  private performFullCleanup() {
    // 2. Limpiar Signals y LocalStorage de todos los servicios
    this.userPrefsService.clearUserData();
    this.userInfoService.clearUserData();

    // 3. Notificar a la app que ya no hay sesión
    this.authService.sendUserSession(false);

    // 4. Redirigir al login o inicio
    this.router.navigate(['/login']);
  }
}
