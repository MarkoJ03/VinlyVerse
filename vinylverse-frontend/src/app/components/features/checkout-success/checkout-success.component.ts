import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NarudzbinaService } from '../../../services/narudzbina.service';
import { Narudzbina } from '../../../models/Narudzbina';

@Component({
  selector: 'app-checkout-success',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './checkout-success.component.html',
  styleUrl: './checkout-success.component.css',
})
export class CheckoutSuccessComponent implements OnInit {
  narudzbina: Narudzbina | null = null;
  greska: string | null = null;
  ucitava = true;

  potvrdaOtvorena = false;
  potvrdaNaslov = '';
  potvrdaPoruka = '';
  private poslePotvrde: (() => void) | null = null;
  pouzecKorisnikPotvrdio = false;

  private pristupniToken: string | null = null;
  private orderId: number | null = null;
  private autoPayPalCapturePokrenut = false;

  constructor(
    private route: ActivatedRoute,
    private narudzbinaService: NarudzbinaService
  ) {}

  ngOnInit(): void {
    const idStr = this.route.snapshot.queryParamMap.get('orderId');
    if (!idStr) {
      this.ucitava = false;
      this.greska = 'Nedostaje broj porudžbine (orderId) u linku. Vratite se u korpu.';
      return;
    }
    const id = +idStr;
    if (Number.isNaN(id) || id <= 0) {
      this.ucitava = false;
      this.greska = 'Neispravan broj porudžbine.';
      return;
    }
    this.orderId = id;
    const tok = sessionStorage.getItem(this.guestTokenKey(id));
    if (!tok) {
      this.ucitava = false;
      this.greska =
        'Nije pronađen pristup ovoj porudžbini u ovom pregledaču. Otvorite stranicu sa istog uređaja nakon naručivanja ili vratite se u korpu.';
      return;
    }
    this.pristupniToken = tok;
    this.narudzbinaService.getGuestPregled(id, tok).subscribe({
      next: (n) => {
        this.narudzbina = n;
        if (this.trebaAutomatskiPayPalCapture(n)) {
          this.autoPayPalCapturePokrenut = true;
          this.izvrsiPayPalCapture();
          return;
        }
        this.ucitava = false;
      },
      error: (err) => {
        this.ucitava = false;
        this.narudzbina = null;
        this.greska = this.porukaGreske(err, 'Porudžbina nije pronađena ili je pristup istekao.');
      },
    });
  }

  private trebaAutomatskiPayPalCapture(narudzbina: Narudzbina): boolean {
    return (
      !this.autoPayPalCapturePokrenut &&
      this.jePovratakSaPayPal() &&
      narudzbina.nacinPlacanja === 'PAYPAL' &&
      !!narudzbina.providerOrderId &&
      narudzbina.status !== 'PAID' &&
      narudzbina.status !== 'CANCELLED'
    );
  }

  private jePovratakSaPayPal(): boolean {
    const params = this.route.snapshot.queryParamMap;
    return params.has('token') || params.has('PayerID');
  }

  otvoriPayPal(): void {
    this.otvoriPotvrdu(
      'Nastaviti na PayPal?',
      'Otvoriće se PayPal da odobrite uplatu. Nastaviti?',
      () => this.izvrsiPayPalCreateOrder()
    );
  }

  private izvrsiPayPalCreateOrder(): void {
    const id = this.orderId;
    if (id == null || !this.pristupniToken) {
      return;
    }
    if (this.narudzbina?.nacinPlacanja !== 'PAYPAL') {
      return;
    }
    this.ucitava = true;
    this.greska = null;
    this.narudzbinaService.createPayPalOrder(id, this.pristupniToken).subscribe({
      next: (res) => {
        this.ucitava = false;
        if (res.approveUrl) {
          window.location.href = res.approveUrl;
        } else {
          this.greska = 'PayPal nije vratio link za odobrenje.';
        }
      },
      error: (err) => {
        this.ucitava = false;
        this.greska = this.porukaGreske(err, 'Greška pri kreiranju PayPal porudžbine.');
      },
    });
  }

  platiMock(): void {
    this.otvoriPotvrdu(
      'Mock uplata (lokalni test)?',
      'Ovo simulira uspešnu uplatu bez pravog novca. Potvrditi?',
      () => this.izvrsiPlatiMock()
    );
  }

  private izvrsiPlatiMock(): void {
    const id = this.orderId;
    if (id == null || !this.pristupniToken) {
      return;
    }
    this.ucitava = true;
    this.greska = null;
    this.narudzbinaService.payMock(id, this.pristupniToken).subscribe({
      next: (n) => {
        this.narudzbina = n;
        this.ucitava = false;
      },
      error: (err) => {
        this.ucitava = false;
        this.greska = this.porukaGreske(err, 'Greška pri mock plaćanju.');
      },
    });
  }

  potvrdiPayPalCapture(): void {
    this.otvoriPotvrdu(
      'Završiti uplatu preko PayPal-a?',
      'Evidentiraćemo uplatu kao plaćenu. Nastaviti?',
      () => this.izvrsiPayPalCapture()
    );
  }

  private izvrsiPayPalCapture(): void {
    const id = this.orderId;
    if (id == null || !this.pristupniToken) {
      return;
    }
    this.ucitava = true;
    this.greska = null;
    this.narudzbinaService.capturePayPal(id, this.pristupniToken).subscribe({
      next: () => {
        this.osvezi();
      },
      error: (err) => {
        this.ucitava = false;
        this.greska = this.porukaGreske(err, 'Greška pri potvrdi PayPal uplate.');
      },
    });
  }

  zapocniPotvrduPouzeca(): void {
    this.otvoriPotvrdu(
      'Potvrdi porudžbinu',
      'Potvrđujete porudžbinu sa plaćanjem pouzećem prilikom preuzimanja?',
      () => {
        this.pouzecKorisnikPotvrdio = true;
      }
    );
  }

  zatvoriPotvrdu(): void {
    this.potvrdaOtvorena = false;
    this.poslePotvrde = null;
  }

  potvrdiDijalog(): void {
    const fn = this.poslePotvrde;
    this.zatvoriPotvrdu();
    fn?.();
  }

  private otvoriPotvrdu(naslov: string, poruka: string, posle: () => void): void {
    this.potvrdaNaslov = naslov;
    this.potvrdaPoruka = poruka;
    this.poslePotvrde = posle;
    this.potvrdaOtvorena = true;
  }

  private osvezi(): void {
    const id = this.orderId;
    const tok = this.pristupniToken;
    if (id == null || !tok) {
      this.ucitava = false;
      return;
    }
    this.narudzbinaService.getGuestPregled(id, tok).subscribe({
      next: (n) => {
        this.narudzbina = n;
        this.ucitava = false;
      },
      error: () => {
        this.ucitava = false;
        if (this.narudzbina) {
          this.narudzbina = { ...this.narudzbina, status: 'PAID' };
        }
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

  private guestTokenKey(narudzbinaId: number): string {
    return `vinylverse_gost_narudzbina_${narudzbinaId}_token`;
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
