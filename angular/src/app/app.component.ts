import { Component } from '@angular/core';
import { FooterComponent } from './core/layout/footer/footer.component';
import { HeaderComponent } from './core/layout/header/header.component';
import { MapPageComponent } from './features/pages/map-page/map-page.component';
import { SearchBarComponent } from './features/pages/search-bar/search-bar.component';
import { PruebaNoSubirComponent } from './features/pages/map-page/pruebaNoSubir';

@Component({
  selector: 'app-root',

  imports: [FooterComponent, HeaderComponent, SearchBarComponent ,SearchBarComponent, MapPageComponent, PruebaNoSubirComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'angular';
}
