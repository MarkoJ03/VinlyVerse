import { Routes } from '@angular/router';
import { authGuard } from './authGuard';

import { HomePageComponent } from './components/features/home-page/home-page.component';
import { AboutPageComponent } from './components/features/about-page/about-page.component';
import { LoginPageComponent } from './components/features/login-page/login-page.component';
import { RegisterAdminPageComponent } from './components/features/register-admin-page/register-admin-page.component';
import { PlocePageComponent } from './components/features/ploce-page/ploce-page.component';
import { PlocaDetailsComponent } from './components/features/ploca-details/ploca-details.component';

import { ZanroviComponent } from './components/features/CRUD/Zanr/zanrovi/zanrovi.component';
import { ZanroviFormaComponent } from './components/features/CRUD/Zanr/zanrovi-forma/zanrovi-forma.component';
import { PloceComponent } from './components/features/CRUD/Ploca/ploce/ploce.component';
import { PlocaFormaComponent } from './components/features/CRUD/Ploca/ploce-forma/ploce-forma.component';
import { NotFoundPageComponent } from './components/features/not-found-page/not-found-page.component';

export const routes: Routes = [
  // PUBLIC ROUTES
  { path: '', component: HomePageComponent },
  { path: 'about', component: AboutPageComponent },
  { path: 'ploce', component: PlocePageComponent },
  { path: 'detalji/:id/:slug', component: PlocaDetailsComponent },
  { path: 'login', component: LoginPageComponent },
  { path: 'register-admin', component: RegisterAdminPageComponent },

  // ADMIN ROUTES
  {
    path: 'admin/zanrovi',
    component: ZanroviComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ROLE_ADMIN'] }
  },
  {
    path: 'admin/zanrovi/forma',
    component: ZanroviFormaComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ROLE_ADMIN'] }
  },
  {
    path: 'admin/zanrovi/izmeni/:id',
    component: ZanroviFormaComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ROLE_ADMIN'] }
  },
  {
    path: 'admin/ploce',
    component: PloceComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ROLE_ADMIN'] }
  },
  {
    path: 'admin/ploce/forma',
    component: PlocaFormaComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ROLE_ADMIN'] }
  },
  {
    path: 'admin/ploce/izmeni/:id',
    component: PlocaFormaComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ROLE_ADMIN'] }
  },
  {
  path: '**',
  component: NotFoundPageComponent
}
];
