import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ploca-card',
  imports: [RouterLink, CommonModule],
  templateUrl: './ploca-card.component.html',
  styleUrl: './ploca-card.component.css'
})
export class PlocaCardComponent {
  @Input() naziv!: string;
  @Input() izdavackaKuca!: string;
  @Input() zanr!: string;
  @Input() cena!: number;
  @Input() slika!: string; 
  @Input() id!: number;
  @Input() slug!: string;
  @Input() prikaziDodajUKorpu = false;
  @Output() dodajUKorpu = new EventEmitter<void>();

  get punaPutanjaSlike(): string {
  return 'http://localhost:8080/' + this.slika;
}

  onDodajUKorpu(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.dodajUKorpu.emit();
  }
}
