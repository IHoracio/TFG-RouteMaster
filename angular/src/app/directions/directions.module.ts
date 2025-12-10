import { NgModule } from '@angular/core';
import {FormsModule} from "@angular/forms"
import { DirectionsComponent } from './directions.component';

@NgModule({
  declarations: [
    DirectionsComponent
  ],
  imports: [
    FormsModule
  ],
  exports: [DirectionsComponent],
  bootstrap: [DirectionsComponent]
})
export class DirectionsModule { }