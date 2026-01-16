import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { AuthService } from '../../../services/auth/auth-service.service';

@Component({
  selector: 'app-header',
  imports: [RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  isLoggedIn: boolean = false;
  constructor(private authService: AuthService) {
  }
  ngOnInit() {
    this.authService.getUserSession().subscribe(
      loggedIn => this.isLoggedIn = loggedIn
    );
    console.log(this.isLoggedIn)

  }
}
