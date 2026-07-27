import { Component, signal, inject, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { TranslationService } from '../../../../services/singleton/translation.service';

@Component({
  selector: 'app-login-prompt',
  templateUrl: './login-prompt.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './login-prompt.component.css'
})
export class LoginPromptComponent {
  @Input() show: boolean = false;
  @Output() close = new EventEmitter<void>();
  translation = inject(TranslationService);
  router = inject(Router);

  onClose() {
    this.close.emit();
  }

  goToLogin() {
    this.onClose();
    this.router.navigate(['/login']);
  }
}