import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [FormsModule, ReactiveFormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  form: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    this.form = formBuilder.group({
      username: ['',
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

  onSubmit() {
    if(this.form.valid){
      console.log(this.form.value)
      /*this.userService.saveUser(this.user).subscribe(response => {
          this.userSent = response;
          console.log(this.userSent)
          this.message = "Usuario creado con éxito."
          this.error = "";
      }, (err)=>{
          this.error = "Ha occurido un error con los datos introducidos."
          this.message = ""
          console.log(err)
      });*/
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
