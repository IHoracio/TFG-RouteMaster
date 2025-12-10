import { Component, Input, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../../../services/routes/route.service';
import { RouteGroupResponse } from '../map-page/Utils/google-route.mapper';
import { Route } from '@angular/router';

@Component({
  selector: 'app-search-bar',
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

  directions: { origin: string, destination: string }

  constructor(private routeService: RouteService) {
    this.directions = { origin: "Madrid", destination: "Madrid" }
  }

  originInput: string = ""
  destinationInput: string = ""

  message: RouteGroupResponse = {
    routes: []
  };
  onSubmit() {
    this.directions.destination = this.destinationInput
    this.directions.origin = this.originInput;

    console.log(this.directions.origin, this.directions.destination)

    this.routeService.calculateCoordinates(this.directions.origin, this.directions.destination)
      .subscribe(data => this.message = JSON.parse(data));
  }

  imprimirMensaje() {
    if (this.message.routes) {
      this.message.routes.forEach((route) => {
        if (route.legs) {
          route.legs.forEach((leg, legIndex) => {
            console.log(`${legIndex}:`);
            console.log(`${leg.distance?.text}`);
            console.log(`${leg.duration?.text}`);
            console.log(`${leg.start_address}`);
            console.log(`${leg.end_address}`);
            if (leg.steps) {
              leg.steps.forEach((step, stepIndex) => {
                console.log(`${stepIndex}:`);
                console.log(`lat=${step.start_location?.lat}, lng=${step.start_location?.lng}`);
              });
            }
          });
        }
      });
    }
  }
}
