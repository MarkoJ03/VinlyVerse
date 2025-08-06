import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { LoginService } from '../../../services/login.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink,CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  menuOpen = false;

  constructor(public loginService: LoginService,public router: Router) {}

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  closeMenu() {
    this.menuOpen = false;
  }

  isAdmin(): boolean {
    return this.loginService.getRoles()?.includes('ROLE_ADMIN');
  }

  isLoggedIn(): boolean {
    return !!this.loginService.token;
  }

  logout() {
  this.loginService.token = null;
  localStorage.removeItem('token');
    this.router.navigateByUrl('/');
}

}
