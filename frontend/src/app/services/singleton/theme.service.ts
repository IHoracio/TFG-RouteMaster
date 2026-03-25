import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private currentTheme = signal<string>('LIGHT');

  constructor() {
    const savedTheme = localStorage.getItem('selectedTheme') || 'LIGHT';
    this.setTheme(savedTheme);
  }

  setTheme(theme: string): void {
    this.currentTheme.set(theme);
    localStorage.setItem('selectedTheme', theme);
    document.body.className = theme.toLowerCase();
  }

  getCurrentTheme(): string {
    return this.currentTheme();
  }

  toggleTheme(): void {
    const newTheme = this.currentTheme() === 'LIGHT' ? 'DARK' : 'LIGHT';
    this.setTheme(newTheme);
  }

  get selectedTheme() {
    return this.currentTheme.asReadonly();
  }
}