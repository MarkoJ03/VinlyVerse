import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../shared/base-table/base-table.component';
import { Router } from '@angular/router';
import { Ploca } from '../../../../../models/Ploca';
import { PlocaService } from '../../../../../services/ploca.service';

@Component({
  selector: 'app-ploce',
  imports: [BaseTableComponent],
  templateUrl: './ploce.component.html',
  styleUrl: './ploce.component.css'
})
export class PloceComponent {

 ploce: Ploca[] = [];
  kolone: string[] = [
    'id',
    'proizvod.naziv',
    'proizvod.cena',
    'proizvod.opis',
    'listaPesama',
    'brend',
    'izdavackaKuca',
    'godinaIzdanja',
    'zanr.naziv',
  ];

  constructor(
    private service: PlocaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.service.getAll().subscribe({
      next: (res) => this.ploce = res,
      error: (err) => console.error('Greška prilikom učitavanja ploča:', err)
    });
  }

  izmeni(ploca: Ploca): void {
    this.router.navigate(['/ploca/izmeni', ploca.id]);
  }

  obrisi(id: number): void {
    this.service.delete(id).subscribe(() => {
      this.ploce = this.ploce.filter(p => p.id !== id);
    });
  }

  detalji(id: number): void {
    this.router.navigate(['/ploca', id]);
  }

  otkazi(): void {
    this.router.navigate(['/ploca']);
  }
}
