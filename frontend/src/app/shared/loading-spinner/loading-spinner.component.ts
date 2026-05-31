import { Component, inject } from '@angular/core';
import { TranslationService } from '../../services/singleton/translation.service';

@Component({
  selector: 'app-loading-spinner',
  imports: [],
  templateUrl: './loading-spinner.component.html',
  styleUrl: './loading-spinner.component.css',
})
export class LoadingSpinnerComponent {
  translation = inject(TranslationService);

}
