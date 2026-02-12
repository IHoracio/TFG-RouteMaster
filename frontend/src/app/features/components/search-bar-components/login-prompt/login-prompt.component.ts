import { Component, signal, inject, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { TranslationService } from '../../../../services/translation.service';

@Component({
  selector: 'app-login-prompt',
  templateUrl: './login-prompt.component.html',
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