import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { User } from '../../../Dto/user';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-create-user',
  imports: [FormsModule, ReactiveFormsModule, NgIf],
  templateUrl: './create-user.component.html',
  styleUrl: './create-user.component.css'
})
export class CreateUserComponent {

  form: FormGroup;

  constructor(private formBuilder: FormBuilder) {
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
        Validators.required,
        //this.passwordMatchValidator
      ]],
      name: ['', [
        Validators.required
      ]],
      surname: ['', [
        Validators.required
      ]]
    })
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    if (password !== confirmPassword) {
      return { passwordMismatch: true };
    }
    return null;
  }
  user: User = {
    email: "",
    password: "",
    name: "",
    surname: ""
  }

  hasError(controlName: string, errorName: string) {
        return (
            this.form.get(controlName)?.hasError(errorName) &&
            this.form.get(controlName)?.touched
        );
  }

  onSubmit() {
    if(this.form.valid){
      console.log(this.form.value)
    } else{
      alert("hay errores!!!")
    }
   }
}
