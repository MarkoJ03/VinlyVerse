import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BaseTableComponent } from '../../../../shared/base-table/base-table.component';
import { Narudzbina } from '../../../../../models/Narudzbina';
import { NarudzbinaService } from '../../../../../services/narudzbina.service';

interface NarudzbinaRed {
  id: number;
  datum: string;
  kupac: string;
  nacinPlacanja: string;
  status: string;
  ukupanIznos: number;
  brojStavki: number;
  statusKod: string;
}

@Component({
  selector: 'app-narudzbine',
  standalone: true,
  imports: [BaseTableComponent, FormsModule],
  templateUrl: './narudzbine.component.html',
  styleUrl: './narudzbine.component.css',
})
export class NarudzbineComponent implements OnInit {
  sveNarudzbine: NarudzbinaRed[] = [];
  narudzbine: NarudzbinaRed[] = [];
  kolone: string[] = ['id', 'datum', 'kupac', 'nacinPlacanja', 'status', 'ukupanIznos', 'brojStavki'];
  filterStatus: 'SVE' | 'POSLATE' | 'NEPOSLATE' = 'SVE';
  ucitava = false;
  greska: string | null = null;

  constructor(
    private narudzbinaService: NarudzbinaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.ucitava = true;
    this.greska = null;
    this.narudzbinaService.getAll().subscribe({
      next: (res) => {
        const sortirane = [...res].sort((a, b) => {
          const ta = a.datumKreiranja ? new Date(a.datumKreiranja).getTime() : 0;
          const tb = b.datumKreiranja ? new Date(b.datumKreiranja).getTime() : 0;
          return tb - ta;
        });
        this.sveNarudzbine = sortirane.map((n) => this.mapirajRed(n));
        this.primeniFilter();
        this.ucitava = false;
      },
      error: (err) => {
        this.ucitava = false;
        this.greska = this.porukaGreske(err, 'Ne mogu da učitam porudžbine.');
      },
    });
  }

  kupacLabel(n: Narudzbina): string {
    if (n.gostEmail) {
      return `Gost: ${n.gostEmail}`;
    }
    if (n.korisnikEmail) {
      return n.korisnikEmail;
    }
    if (n.korisnikId != null) {
      return `Korisnik #${n.korisnikId}`;
    }
    return 'Nepoznato';
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

  private mapirajRed(n: Narudzbina): NarudzbinaRed {
    return {
      id: n.id ?? 0,
      datum: n.datumKreiranja ? this.formatDatum(n.datumKreiranja) : '—',
      kupac: this.kupacLabel(n),
      nacinPlacanja: n.nacinPlacanja === 'PAYPAL' ? 'PayPal' : 'Pouzećem',
      status: this.statusTekst(n.status),
      ukupanIznos: n.ukupanIznos ?? 0,
      brojStavki: n.stavke?.length ?? 0,
      statusKod: n.status ?? '',
    };
  }

  promenaFiltera(): void {
    this.primeniFilter();
  }

  otvoriDetalje(id: number): void {
    this.router.navigate(['/admin/narudzbine', id]);
  }

  private primeniFilter(): void {
    if (this.filterStatus === 'POSLATE') {
      this.narudzbine = this.sveNarudzbine.filter((n) => n.statusKod === 'POSLATA');
      return;
    }
    if (this.filterStatus === 'NEPOSLATE') {
      this.narudzbine = this.sveNarudzbine.filter((n) => n.statusKod !== 'POSLATA');
      return;
    }
    this.narudzbine = [...this.sveNarudzbine];
  }

  private formatDatum(iso: string): string {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) {
      return iso;
    }
    const dd = String(d.getDate()).padStart(2, '0');
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const yyyy = d.getFullYear();
    const hh = String(d.getHours()).padStart(2, '0');
    const mi = String(d.getMinutes()).padStart(2, '0');
    return `${dd}.${mm}.${yyyy} ${hh}:${mi}`;
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
