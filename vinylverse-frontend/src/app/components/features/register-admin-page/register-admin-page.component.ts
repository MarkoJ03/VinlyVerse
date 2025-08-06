import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../../services/login.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register-admin-page',
  imports: [FormsModule,CommonModule],
  templateUrl: './register-admin-page.component.html',
  styleUrl: './register-admin-page.component.css'
})
export class RegisterAdminPageComponent {

  email = '';
  lozinka = '';
  korisnickoIme = '';
  errorMessage = '';
  successMessage = '';

  constructor(private loginService: LoginService, private router: Router) {}

  register() {
    const adminData = {
      email: this.email,
      lozinka: this.lozinka,
      korisnickoIme: this.korisnickoIme,
    };

    this.loginService.registerAdmin(adminData).subscribe({
      next: () => {
        this.successMessage = 'Admin uspešno registrovan!';
        this.errorMessage = '';
      },
      error: () => {
        this.errorMessage = 'Došlo je do greške prilikom registracije.';
        this.successMessage = '';
      }
    });
  }
}
