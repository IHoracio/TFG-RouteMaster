import { Component, Input, NgModule} from '@angular/core';
import { DirectionsInfo } from '../../../directions/directions-info';
import { FormsModule } from '@angular/forms';
import { DirectionsModule } from '../../../directions/directions.module';
import { setThrowInvalidWriteToSignalError } from '@angular/core/primitives/signals';


@Component({
  selector: 'app-search-bar',
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

  directions: {origin:string, destination:string}
  
  constructor(){
    this.directions = {origin:"Madrid", destination:"Madrid"}
  }

  originInput: string = ""
  destinationInput:string = ""

  onSubmit(){
    this.directions.destination = this.destinationInput
    this.directions.origin = this.originInput;

    console.log( this.directions.origin, this.directions.destination)
  }

}
