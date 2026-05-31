import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { User } from '../../Dto/user-dtos';
import { UserService } from '../../services/user/user.service';
import { RouterLink } from "@angular/router";
import { AuthService } from '../../services/auth/auth-service.service';
import { TranslationService } from '../../services/singleton/translation.service';

@Component({
  selector: 'app-create-user',
  imports: [FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create-user.component.html',
  styleUrl: './create-user.component.css'
})
export class CreateUserComponent {

  translation = inject(TranslationService)

  form: FormGroup;

  user: User = {
    email: "",
    password: "",
    passwordConfirmation: "",
    name: "",
    surname: ""
  }

  constructor(private formBuilder: FormBuilder, private userService: UserService, private authService: AuthService) {
    this.form = formBuilder.group({
      email: ['',
        [
          Validators.required,
          Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/),
        ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\W)/)
      ]],
      passwordConfirmation: ['', [
        Validators.required
      ]],
      name: ['', [
        Validators.required
      ]],
      surname: ['', [
        Validators.required
      ]]
    }, { validator: this.passwordMatchValidator })
  }

  get email() { return this.form.get('email'); }
  get password() { return this.form.get('password'); }
  get passwordConfirmation() { return this.form.get('passwordConfirmation'); }
  get name() { return this.form.get('name'); }
  get surname() { return this.form.get('surname'); }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const passwordConfirmation = form.get('passwordConfirmation')?.value;
    if (password !== passwordConfirmation) {
      return { passwordMismatch: true };
    }
    return null;
  }

  hasError(controlName: string, errorName: string) {
    return (
      this.form.get(controlName)?.hasError(errorName) &&
      this.form.get(controlName)?.touched
    );
  }
  message: string = "";
  error: string = "";
  onSubmit() {
    if (this.form.valid) {
      this.user.email = this.email?.value;
      this.user.password = this.password?.value;
      this.user.passwordConfirmation = this.passwordConfirmation?.value;
      this.user.name = this.name?.value;
      this.user.surname = this.surname?.value;

      console.log(this.user);

      this.authService.saveUser(this.user).subscribe({
        next: () => {
          // Usamos también la traducción para el mensaje de éxito
          this.message = this.translation.translate('register.successMessage');
          this.error = "";
          this.form.reset(); // Opcional: limpia el formulario al tener éxito
        },
        error: (err) => {
          this.message = "";

          // Evaluamos el código de estado HTTP que devuelve el backend
          switch (err.status) {
            case 400:
              this.error = this.translation.translate('register.errorBadRequest');
              break;
            case 409:
              this.error = this.translation.translate('register.errorUserExists');
              break;
            default:
              // Para cualquier otra cosa (como un error 500 o si el servidor está caído)
              this.error = this.translation.translate('register.errorGeneric');
              break;
          }
        }
      });
    } else {
      console.log("El formulario tiene errores.");
    }
  }
}
