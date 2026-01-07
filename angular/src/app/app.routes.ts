import { Routes } from '@angular/router';
import { SearchBarComponent } from './features/pages/search-bar/search-bar.component';
import { LandingPageComponent } from './core/layout/landing-page/landing-page.component';
import { UserPageComponent } from './features/pages/user-page/user-page.component';

export const routes: Routes = [
    {path: "", component: LandingPageComponent},
    {path: "search", component: SearchBarComponent},
    {path: "user", component: UserPageComponent}
];
