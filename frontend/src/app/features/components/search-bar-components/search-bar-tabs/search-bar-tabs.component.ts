import { Component, inject, input, output } from '@angular/core';
import { NgClass } from '@angular/common';
import { TranslationService } from '../../../../services/translation.service';

@Component({
  selector: 'app-search-bar-tabs',
  standalone: true,
  imports: [NgClass],
  templateUrl: './search-bar-tabs.component.html',
  styleUrl: './search-bar-tabs.component.css'
})
export class SearchBarTabsComponent {
  activeTab = input<string>('destination');
  tabChange = output<string>();

  translation = inject(TranslationService);

  setTab(tab: string) {
    this.tabChange.emit(tab);
  }
}
