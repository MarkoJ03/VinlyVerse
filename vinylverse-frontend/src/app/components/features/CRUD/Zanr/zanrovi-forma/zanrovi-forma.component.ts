import { Component, OnInit } from '@angular/core';
import { ZanrService } from '../../../../../services/zanr.service';
import { Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { FormaModel } from '../../../../../models/FormaModel';
import { Zanr } from '../../../../../models/Zanr';
import { BaseFormComponent } from '../../../../shared/base-form/base-form.component';

@Component({
  selector: 'app-zanrovi-forma',
  imports: [BaseFormComponent],
  templateUrl: './zanrovi-forma.component.html',
  styleUrl: './zanrovi-forma.component.css'
})
export class ZanroviFormaComponent implements OnInit {
  formaModel: FormaModel | null = null;
  idT: number | null = null;

  constructor(
    private service: ZanrService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.idT = id ? +id : null;

    if (this.idT) {
      this.service.getById(this.idT).subscribe(p => {
        this.formaModel = this.kreirajModel(p);
      });
    } else {
      this.formaModel = this.kreirajModel();
    }
  }

  sacuvaj(vrednosti: any): void {
    const akcija = this.idT
      ? this.service.update(this.idT, vrednosti)
      : this.service.create(vrednosti);

    akcija.subscribe({
      next: () => this.router.navigate(['/admin/zanrovi']),
      error: err => console.error('Greška:', err)
    });
  }

  private kreirajModel(t?: Zanr): FormaModel {
    return {
      naziv: t ? 'Izmena zanra' : 'Dodavanje zanra',
      polja: [
        { naziv: 'naziv', labela: 'Naziv', tip: 'text', podrazumevanaVrednost: t?.naziv ?? '', validatori: [Validators.required] }
      ]
    };
  }

  otkazi(): void {
    this.router.navigate(['/admin/zanrovi']);
  }
}