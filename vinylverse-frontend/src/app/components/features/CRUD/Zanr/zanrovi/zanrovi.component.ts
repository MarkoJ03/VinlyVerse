import { Component } from '@angular/core';
import { ZanrService } from '../../../../../services/zanr.service';
import { Zanr } from '../../../../../models/Zanr';
import { Router } from '@angular/router';
import { BaseTableComponent } from '../../../../shared/base-table/base-table.component';

@Component({
  selector: 'app-zanrovi',
  imports: [BaseTableComponent],
  templateUrl: './zanrovi.component.html',
  styleUrl: './zanrovi.component.css'
})
export class ZanroviComponent {

zanrovi: Zanr[] = [];
  kolone: string[] = ['id', 'naziv'];

  constructor(
    private service: ZanrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.service.getAll().subscribe({
      next: (res) => this.zanrovi = res,
      error: (err) => console.error('Greška prilikom učitavanja zanrovi:', err)
    });
  }

  izmeni(zanr: Zanr): void {
    this.router.navigate(['/zanr/izmeni', zanr.id]);
  }
  obrisi(id: number): void {
    this.service.delete(id).subscribe(() => {
      this.zanrovi = this.zanrovi.filter(v => v.id !== id);
    });
  }

  detalji(id: number): void {
    this.router.navigate(['/zanr', id]);
  }
  
  otkazi(): void {
    this.router.navigate(['/zanr']);
  }
}
