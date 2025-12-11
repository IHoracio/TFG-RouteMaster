import { Component, Input, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../../../services/routes/route.service';
import { RouteGroupResponse } from '../map-page/Utils/google-route.mapper';


@Component({
  selector: 'app-search-bar',
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {


  constructor(private routeService: RouteService) {
  }

  origin: string = ""
  destination: string = ""

  esPeruano:boolean = true;

  message: RouteGroupResponse = {
    routes: []
  };
  onSubmit() {
    this.routeService.calculateRoute(this.origin, this.destination)
      .subscribe(data => this.message = JSON.parse(data));
  }

  imprimirMensaje() {
    console.log(JSON.stringify(this.message));
  }
}
