import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { User } from '../../Dto/user-dtos';
import { NgIf } from '@angular/common';
import { UserService } from '../../services/user/user.service';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-create-user',
  imports: [FormsModule, ReactiveFormsModule, NgIf, RouterLink],
  templateUrl: './create-user.component.html',
  styleUrl: './create-user.component.css'
})
export class CreateUserComponent {

  form: FormGroup;

  user: User = {
    email: "",
    password: "",
    name: "",
    surname: ""
  }

  constructor(private formBuilder: FormBuilder, private userService: UserService) {
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
      confirmPassword: ['', [
        Validators.required
      ]],
      name: ['', [
        Validators.required
      ]],
      surname: ['', [
        Validators.required
      ]]
    }, {validator: this.passwordMatchValidator})
  }

  get email() { return this.form.get('email'); }
  get password() { return this.form.get('password'); }
  get confirmPassword() { return this.form.get('confirmPassword'); }
  get name() { return this.form.get('name'); }
  get surname() { return this.form.get('surname'); }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    if (password !== confirmPassword) {
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
  userSent: User = {
    email: "",
    password: "",
    name: "",
    surname: ""
  }
  message: string = "";
  error: string = "";
  onSubmit() {
    if(this.form.valid){
      this.user.email = this.email?.value;
      this.user.password = this.password?.value;
      this.user.name = this.name?.value;
      this.user.surname = this.surname?.value;

      console.log(this.user)
      this.userService.saveUser(this.user).subscribe(response => {
          this.userSent = response;
          console.log(this.userSent)
          this.message = "Usuario creado con éxito."
          this.error = "";
      }, (err)=>{
          this.error = "Ha occurido un error con los datos introducidos."
          this.message = ""
          console.log(err)
      });
    } else{
      console.log("El formulario tiene errores.")
    }
   }
}
