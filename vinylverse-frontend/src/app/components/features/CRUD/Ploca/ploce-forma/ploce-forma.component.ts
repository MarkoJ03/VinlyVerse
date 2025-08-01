import { Component, OnInit } from '@angular/core';
import { Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { FormaModel } from '../../../../../models/FormaModel';
import { ZanrService } from '../../../../../services/zanr.service';
import { PlocaService } from '../../../../../services/ploca.service';
import { Zanr } from '../../../../../models/Zanr';
import { BaseFormComponent } from '../../../../shared/base-form/base-form.component';
import { Ploca } from '../../../../../models/Ploca';
import { ProizvodService } from '../../../../../services/proizvod.service';

@Component({
  selector: 'app-ploca-forma',
  standalone: true,
  imports: [BaseFormComponent],
  templateUrl: './ploce-forma.component.html',
  styleUrl: './ploce-forma.component.css'
})
export class PlocaFormaComponent implements OnInit {
  formaModel: FormaModel | null = null;
  idT: number | null = null;
  zanrovi: Zanr[] = [];
  slika: File | null = null;

  constructor(
    private zanrService: ZanrService,
    private plocaService: PlocaService,
    private router: Router,
    private route: ActivatedRoute,
    private proizvodService: ProizvodService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.idT = id ? +id : null;

    this.zanrService.getAll().subscribe(z => {
      this.zanrovi = z;

      if (this.idT) {
        this.plocaService.getById(this.idT).subscribe(p => {
          this.formaModel = this.kreirajModel(p);
        });
      } else {
        this.formaModel = this.kreirajModel();
      }
    });
  }

  uploadujSliku(): Promise<string> {
  return new Promise((resolve, reject) => {
    if (!this.slika) {
      resolve('');
      return;
    }

    this.proizvodService.uploadSlika(this.slika).subscribe({
      next: path => resolve(path),
      error: err => reject(err)
    });
  });
}

async sacuvaj(vrednosti: any): Promise<void> {
  try {
    const putanja = await this.uploadujSliku();

    const dto: Ploca = {
      id: this.idT ?? undefined,
      proizvod: {
        naziv: vrednosti.proizvod_naziv,
        cena: vrednosti.proizvod_cena,
        opis: vrednosti.proizvod_opis,
        slikaPutanja: putanja || vrednosti.proizvod_slikaPutanja, 
        vidljiv: vrednosti.proizvod_vidljiv
      },
      listaPesama: vrednosti.listaPesama,
      brend: vrednosti.brend,
      izdavackaKuca: vrednosti.izdavackaKuca,
      godinaIzdanja: vrednosti.godinaIzdanja,
      zanr: vrednosti.zanr,
      vidljiv: vrednosti.vidljiv
    };

    const akcija = this.idT
      ? this.plocaService.update(this.idT, dto)
      : this.plocaService.create(dto);

    akcija.subscribe({
      next: () => this.router.navigate(['/ploca']),
      error: err => console.error('Greška pri snimanju ploče:', err)
    });
  } catch (err) {
    console.error('Greška pri uploadu slike:', err);
  }
}


  otkazi(): void {
    this.router.navigate(['/ploca']);
  }

  private kreirajModel(p?: Ploca): FormaModel {
    return {
      naziv: p ? 'Izmena ploče' : 'Dodavanje ploče',
      polja: [
        // PROIZVOD
        { naziv: 'proizvod_naziv', labela: 'Naziv proizvoda', tip: 'text', podrazumevanaVrednost: p?.proizvod?.naziv ?? '', validatori: [Validators.required] },
        { naziv: 'proizvod_cena', labela: 'Cena', tip: 'text', podrazumevanaVrednost: p?.proizvod?.cena ?? '', validatori: [Validators.required] },
        { naziv: 'proizvod_opis', labela: 'Opis', tip: 'text', podrazumevanaVrednost: p?.proizvod?.opis ?? '', validatori: [Validators.required] },
        { naziv: 'proizvod_vidljiv', labela: '', tip: 'hidden', podrazumevanaVrednost: true },

        // PLOCA
        { naziv: 'listaPesama', labela: 'Lista pesama', tip: 'text', podrazumevanaVrednost: p?.listaPesama ?? '', validatori: [Validators.required] },
        { naziv: 'brend', labela: 'Brend', tip: 'text', podrazumevanaVrednost: p?.brend ?? '', validatori: [Validators.required] },
        { naziv: 'izdavackaKuca', labela: 'Izdavačka kuća', tip: 'text', podrazumevanaVrednost: p?.izdavackaKuca ?? '', validatori: [Validators.required] },
        { naziv: 'godinaIzdanja', labela: 'Godina izdanja', tip: 'text', podrazumevanaVrednost: p?.godinaIzdanja ?? '', validatori: [Validators.required] },
        {
          naziv: 'zanr',
          labela: 'Žanr',
          tip: 'select',
          podrazumevanaVrednost: p?.zanr ?? null,
          opcije: this.zanrovi,
          displayFn: (z: Zanr) => z.naziv
        },
        { naziv: 'vidljiv', labela: '', tip: 'hidden', podrazumevanaVrednost: true }
      ]
    };
  }

  onFileSelected(event: Event): void {
  const input = event.target as HTMLInputElement;
  if (input?.files?.length) {
    this.slika = input.files[0];
  }
}
}
