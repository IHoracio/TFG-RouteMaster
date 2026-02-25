import { Component, effect, inject } from '@angular/core';
import { FooterComponent } from './core/layout/footer/footer.component';
import { HeaderComponent } from './core/layout/header/header.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',

  imports: [FooterComponent, HeaderComponent, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  themeService = inject(ThemeService);
  title = 'angular';
  constructor() {
    effect(() => {
      const currentTheme = this.themeService.getCurrentTheme();
      document.body.className = currentTheme.toLowerCase();
    });
  }
}
