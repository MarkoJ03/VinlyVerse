import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LoginService } from '../../../services/login.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent {
  email = '';
  lozinka = '';
  errorMessage = '';

  constructor(private loginService: LoginService, private router: Router) {}

  login() {
    const user = { email: this.email, lozinka: this.lozinka };
    this.loginService.login(user).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.errorMessage = 'Neuspešan login. Proveri podatke.'
    });
  }
}
