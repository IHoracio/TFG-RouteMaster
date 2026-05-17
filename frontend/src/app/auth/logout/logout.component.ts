import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth/auth-service.service';

@Component({
  selector: 'app-logout',
  imports: [],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.css'
})
export class LogoutComponent implements OnInit {
  private authService = inject(AuthService);

  ngOnInit() {
    // 1. Llamada al servidor para invalidar sesión (Cookie/Token)
    this.authService.logout().subscribe({
      next: () => this.performFullCleanup(),
      error: () => this.performFullCleanup()
    });
  }

  performFullCleanup() {
    // 1. Limpieza de datos (Síncrono)
    this.authService.sendUserSession(false);
    localStorage.clear();
    sessionStorage.clear();

    window.location.href = '/login';
  }
}
