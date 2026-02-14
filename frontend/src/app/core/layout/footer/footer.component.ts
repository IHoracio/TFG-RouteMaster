import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { TranslationService } from '../../../services/translation.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-footer',
  imports: [DatePipe],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  today: number = Date.now();
  translation = inject(TranslationService)
  router = inject(Router);

  navigateToPrivacy() {
    this.router.navigate(['/privacy']).then(() => {
      window.scrollTo(0, 0);
    });
  }

  navigateToTerms() {
    this.router.navigate(['/terms']).then(() => {
      window.scrollTo(0, 0);
    });
  }
}
