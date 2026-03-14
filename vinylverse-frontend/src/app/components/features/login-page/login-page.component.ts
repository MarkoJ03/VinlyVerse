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
  countdown = 0;
  private countdownInterval: any;

  constructor(private loginService: LoginService, private router: Router) {}

  startCountdown(seconds: number) {
    this.countdown = seconds;
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
    this.countdownInterval = setInterval(() => {
      this.countdown--;
      if (this.countdown <= 0) {
        clearInterval(this.countdownInterval);
        this.countdownInterval = null;
        this.errorMessage = '';
      }
    }, 1000);
  }

  login() {
    if (this.countdown > 0) {
      return;
    }
    const user = { email: this.email, lozinka: this.lozinka };
    this.loginService.login(user).subscribe({
      next: () => {
        this.errorMessage = '';
        this.countdown = 0;
        if (this.countdownInterval) {
          clearInterval(this.countdownInterval);
          this.countdownInterval = null;
        }
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        if (error.status === 401) {
          this.errorMessage = 'Neuspešan login. Proveri podatke.';
        } else if (error.status === 429) {
          this.errorMessage = 'Previše pokušaja. Pokušaj ponovo kasnije.';
          this.startCountdown(60);
        } else {
          this.errorMessage = 'Došlo je do greške. Pokušaj ponovo kasnije.';
        }
      },
    });
  }
}
