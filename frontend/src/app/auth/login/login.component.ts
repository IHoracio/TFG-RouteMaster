import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserLoginDTO } from '../../Dto/user-dtos';
import { UserService } from '../../services/user/user.service';
import { AuthService } from '../../services/auth/auth-service.service';
import { UserDataService } from '../../services/singleton/user-data.service';
import { TranslationService } from '../../services/singleton/translation.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './login.component.css'
})
export class LoginComponent {
  /**
   * Devuelve true si el control tiene el error indicado
   */
  hasError(controlName: string, errorName: string): boolean {
    const control = this.form.get(controlName);
    return !!(control && control.touched && control.hasError(errorName));
  }

  translation = inject(TranslationService);
  userDataService = inject(UserDataService);

  form: FormGroup;
  userLogin: UserLoginDTO = {
    user: "",
    password: ""
  }
  constructor(private formBuilder: FormBuilder, private userService: UserService, private router: Router, private authService: AuthService) {
    this.form = formBuilder.group({
      user: ['',
        [
          Validators.required,
          Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/)
        ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\W)/)
      ]]
    })
  }
  get user() { return this.form.get('user'); }
  get password() { return this.form.get('password'); }
  error: string = "";
  message: string = "";
  onSubmit() {
    if (this.form.invalid) return;

    this.userLogin.user = this.user?.value;
    this.userLogin.password = this.password?.value;

    this.authService.loginUser(this.userLogin).subscribe({
      next: (response: any) => {
        // Limpiamos errores previos si los hubiera
        this.error = "";

        // Si el backend devuelve 204, significa que ya tenía la sesión iniciada
        if (response && response.status === 204) {
          this.error = this.translation.translate('login.errorAlreadyLoggedIn');
          // Aquí decides si quieres redirigir igualmente o dejarlo en la pantalla con el aviso
          return;
        }

        // Si es un 200 OK, procedemos con el inicio de sesión normal
        // 1. Marcamos la sesión como activa
        this.authService.sendUserSession(true);

        // 2. Cargamos el Singleton
        this.userDataService.initApp().subscribe({
          next: (success) => {
            if (success) {
              this.router.navigate(['/']);
            } else {
              this.error = this.translation.translate('login.errorLoadingData');
            }
          },
          error: () => this.error = this.translation.translate('login.errorSyncCritical')
        });
      },
      error: (err) => {
        // Aquí manejamos los errores HTTP (400, 401, etc.)
        switch (err.status) {
          case 400:
            this.error = this.translation.translate('login.errorMissingCredentials');
            break;
          case 401:
            this.error = this.translation.translate('login.errorInvalidCredentials');
            break;
          default:
            this.error = this.translation.translate('login.errorGeneric');
            break;
        }
      }
    });
  }
}