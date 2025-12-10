import { Component, Input, NgModule} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../../../services/routes/route.service';


@Component({
  selector: 'app-search-bar',
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

  directions: {origin:string, destination:string}
  
  constructor(private routeService: RouteService){
    this.directions = {origin:"Madrid", destination:"Madrid"}
  }

  originInput: string = ""
  destinationInput:string = ""

  message: string = '';
  onSubmit(){
    this.directions.destination = this.destinationInput
    this.directions.origin = this.originInput;

    console.log( this.directions.origin, this.directions.destination)

    this.routeService.calculateCoordinates(this.directions.origin, this.directions.destination)
    .subscribe(data => this.message = data);
  }
}
