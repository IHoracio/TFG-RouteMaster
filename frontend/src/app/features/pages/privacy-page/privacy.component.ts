import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { TranslationService } from '../../../services/singleton/translation.service';

@Component({
  selector: 'app-privacy',
  templateUrl: './privacy.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './privacy.component.css'
})
export class PrivacyComponent {
  translation = inject(TranslationService);
}