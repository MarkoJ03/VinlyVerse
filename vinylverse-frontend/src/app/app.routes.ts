import { Routes } from '@angular/router';
import { HeaderComponent } from './components/core/header/header.component';
import { HomePageComponent } from './components/features/home-page/home-page.component';
import { AboutPageComponent } from './components/features/about-page/about-page.component';
import { PlocaDetailsComponent } from './components/features/ploca-details/ploca-details.component';
import { AppComponent } from './app.component';

export const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'about', component: AboutPageComponent },
  {
    path: 'detalji/:id/:slug',
    component: PlocaDetailsComponent
  }

];
