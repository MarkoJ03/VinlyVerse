import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NarudzbinaService } from '../../../../../services/narudzbina.service';
import { Narudzbina } from '../../../../../models/Narudzbina';

@Component({
  selector: 'app-narudzbina-detalji',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './narudzbina-detalji.component.html',
  styleUrl: './narudzbina-detalji.component.css',
})
export class NarudzbinaDetaljiComponent implements OnInit {
  narudzbina: Narudzbina | null = null;
  ucitava = true;
  menjaStatus = false;
  greska: string | null = null;

  private id: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private narudzbinaService: NarudzbinaService
  ) {}

  ngOnInit(): void {
    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? +idStr : NaN;
    if (Number.isNaN(id) || id <= 0) {
      this.ucitava = false;
      this.greska = 'Neispravan ID porudžbine.';
      return;
    }
    this.id = id;
    this.ucitaj();
  }

  oznaciKaoPoslatu(): void {
    if (!this.narudzbina || this.id == null || this.narudzbina.status === 'POSLATA') {
      return;
    }
    this.menjaStatus = true;
    this.greska = null;
    this.narudzbinaService.update(this.id, { ...this.narudzbina, status: 'POSLATA' }).subscribe({
      next: (n) => {
        this.narudzbina = n;
        this.menjaStatus = false;
      },
      error: (err) => {
        this.menjaStatus = false;
        this.greska = this.porukaGreske(err, 'Greška pri ažuriranju statusa.');
      },
    });
  }

  statusTekst(engl: string | null | undefined): string {
    if (!engl) {
      return '—';
    }
    const map: Record<string, string> = {
      CREATED: 'Kreirano',
      POTVRDJENA: 'Potvrđena',
      POSLATA: 'Poslata',
      PENDING_PAYMENT: 'Čeka uplatu',
      PAID: 'Plaćeno',
      FAILED: 'Neuspešno',
      CANCELLED: 'Otkazano',
    };
    return map[engl] || engl;
  }

  private ucitaj(): void {
    if (this.id == null) {
      return;
    }
    this.ucitava = true;
    this.greska = null;
    this.narudzbinaService.getById(this.id).subscribe({
      next: (n) => {
        this.narudzbina = n;
        this.ucitava = false;
      },
      error: (err) => {
        this.ucitava = false;
        this.greska = this.porukaGreske(err, 'Ne mogu da učitam porudžbinu.');
      },
    });
  }

  private porukaGreske(err: unknown, podrazumevano: string): string {
    const e = err as { error?: { message?: string } | string; message?: string };
    if (e?.error && typeof e.error === 'object' && e.error.message) {
      return String(e.error.message);
    }
    if (typeof e?.error === 'string' && e.error.length > 0) {
      return e.error;
    }
    if (e?.message) {
      return e.message;
    }
    return podrazumevano;
  }
}
