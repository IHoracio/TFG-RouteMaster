import { Component } from '@angular/core';
import { UserInfoComponent } from './user-info/user-info.component';
import { UserPreferencesComponent } from './user-preferences/user-preferences.component';

@Component({
  selector: 'app-user-page',
  imports: [UserInfoComponent, UserPreferencesComponent],
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.css'
})
export class UserPageComponent {

}
