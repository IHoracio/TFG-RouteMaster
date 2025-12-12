import { Routes } from '@angular/router';
import { SearchBarComponent } from './features/pages/search-bar/search-bar.component';
import { MapPageComponent } from './features/pages/map-page/map-page.component';
import { LandingPageComponent } from './core/layout/landing-page/landing-page.component';

export const routes: Routes = [
    {path: "", component: LandingPageComponent},
    {path: "search", component: SearchBarComponent},
    {path: "map", component: MapPageComponent}
];
