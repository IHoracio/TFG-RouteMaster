import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserLoginDTO } from '../../Dto/user-dtos';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, ReactiveFormsModule, NgIf, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  form: FormGroup;
  userLogin: UserLoginDTO = {
    user: "",
    password: ""
  }
  constructor(private formBuilder: FormBuilder, private userService: UserService) {
    this.form = formBuilder.group({
      user: ['',
        [
          Validators.required,
        ]],
      password: ['', [
        Validators.required,
        //Validators.minLength(8),
        //Validators.pattern(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\W)/)
      ]]
    })
  }
  get user() { return this.form.get('user'); }
  get password() { return this.form.get('password'); }


  message: string = "";
  error: string = "";
  onSubmit() {
    if(this.form.valid){
      console.log(this.form.value)
      this.userLogin.user = this.user?.value
      this.userLogin.password = this.password?.value
      this.userService.loginUser(this.userLogin).subscribe(response => {
          this.message = "Usuario creado con éxito."
          this.error = "";
          console.log(response)
      }, (err)=>{
          this.error = "Ha occurido un error con los datos introducidos."
          this.message = ""
          console.log(err)
      });
    } else{
      console.log("El formulario tiene errores.")
    }
   }

   hasError(controlName: string, errorName: string) {
        return (
            this.form.get(controlName)?.hasError(errorName) &&
            this.form.get(controlName)?.touched
        );
  }
}
