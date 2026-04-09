import { Component, inject } from '@angular/core';
import { TranslationService } from '../../../services/singleton/translation.service';

@Component({
  selector: 'app-terms',
  templateUrl: './terms.component.html',
  styleUrl: './terms.component.css'
})
export class TermsComponent {
  translation = inject(TranslationService);
}