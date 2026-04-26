import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-checkout-cancel',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './checkout-cancel.component.html',
  styleUrl: './checkout-cancel.component.css',
})
export class CheckoutCancelComponent {}
