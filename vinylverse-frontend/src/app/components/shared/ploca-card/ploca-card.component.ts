import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-ploca-card',
  imports: [],
  templateUrl: './ploca-card.component.html',
  styleUrl: './ploca-card.component.css'
})
export class PlocaCardComponent {
  @Input() naslov!: string;
  @Input() izvodjac!: string;
  @Input() godina!: number;
  @Input() cena!: number;
  @Input() slika!: string; // putanja do slike
}
