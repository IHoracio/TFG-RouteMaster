import { Component } from '@angular/core';
import { FooterComponent } from './core/layout/footer/footer.component';
import { HeaderComponent } from './core/layout/header/header.component';
import { SearchBarComponent } from './features/pages/search-bar/search-bar.component';


@Component({
  selector: 'app-root',
  imports: [FooterComponent, HeaderComponent, SearchBarComponent ,SearchBarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'angular';
}
