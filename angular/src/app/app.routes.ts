import { Routes } from '@angular/router';
import { SearchBarComponent } from './features/pages/search-bar/search-bar.component';
import { LandingPageComponent } from './core/layout/landing-page/landing-page.component';
import { CreateUserComponent } from './features/pages/create-user/create-user.component';

export const routes: Routes = [
    {path: "", component: LandingPageComponent},
    {path: "register-user", component: CreateUserComponent},
    {path: "search", component: SearchBarComponent},
    /*{path: "map", component: MapPageComponent}*/
];
