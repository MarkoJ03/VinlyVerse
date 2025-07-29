import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-ploca-card',
  imports: [RouterLink],
  templateUrl: './ploca-card.component.html',
  styleUrl: './ploca-card.component.css'
})
export class PlocaCardComponent {
  @Input() naziv!: string;
  @Input() izdavackaKuca!: string;
  @Input() zanr!: string;
  @Input() cena!: number;
  @Input() slika!: string; // putanja do slike
  @Input() id!: number;
  @Input() slug!: string;
}
