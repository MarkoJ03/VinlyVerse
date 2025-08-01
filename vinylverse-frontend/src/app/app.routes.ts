import { Routes } from '@angular/router';
import { HeaderComponent } from './components/core/header/header.component';
import { HomePageComponent } from './components/features/home-page/home-page.component';
import { AboutPageComponent } from './components/features/about-page/about-page.component';
import { PlocaDetailsComponent } from './components/features/ploca-details/ploca-details.component';
import { AppComponent } from './app.component';
import { ZanroviComponent } from './components/features/CRUD/Zanr/zanrovi/zanrovi.component';
import { ZanroviFormaComponent } from './components/features/CRUD/Zanr/zanrovi-forma/zanrovi-forma.component';
import { PloceComponent } from './components/features/CRUD/Ploca/ploce/ploce.component';
import { PlocaFormaComponent } from './components/features/CRUD/Ploca/ploce-forma/ploce-forma.component';

export const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'about', component: AboutPageComponent },
  {
    path: 'detalji/:id/:slug',
    component: PlocaDetailsComponent
  },
  {path: 'zanr',
    component: ZanroviComponent
  },
  {path: 'zanrForma',
    component: ZanroviFormaComponent
  },
  {path: 'zanr/izmeni/:id',
    component: ZanroviFormaComponent
  },
  {path:'ploca',
    component:PloceComponent
  },
  {path:'plocaForma',
    component: PlocaFormaComponent},
  {path:'ploca/izmeni/:id',
    component: PlocaFormaComponent}

];
