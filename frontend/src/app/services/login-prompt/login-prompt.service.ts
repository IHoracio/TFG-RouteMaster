import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginPromptService {
  showLoginPrompt = signal(false);

  openLoginPrompt() {
    this.showLoginPrompt.set(true);
  }

  closeLoginPrompt() {
    this.showLoginPrompt.set(false);
  }
}
