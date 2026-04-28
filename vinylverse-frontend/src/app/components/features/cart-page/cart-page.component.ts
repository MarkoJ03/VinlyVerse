import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { CartService } from '../../../services/cart.service';
import { NarudzbinaService } from '../../../services/narudzbina.service';
import { Narudzbina, NacinPlacanjaKod } from '../../../models/Narudzbina';

@Component({
  selector: 'app-cart-page',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './cart-page.component.html',
  styleUrl: './cart-page.component.css',
})
export class CartPageComponent implements OnInit, OnDestroy {
  ucitava = false;

  toastVisible = false;
  toastPoruka = '';
  toastTip: 'warn' | 'error' = 'warn';
  private toastHideTimer?: ReturnType<typeof setTimeout>;
  private alive = true;

  gostEmail = '';
  nacinPlacanja: NacinPlacanjaKod = 'POUZEC';
  adresaIme = '';
  adresaUlica = '';
  adresaGrad = '';
  adresaPostanskiBroj = '';
  adresaDrzava = 'Srbija';
  adresaTelefon = '';

  private paramSub?: Subscription;

  constructor(
    public cartService: CartService,
    private narudzbinaService: NarudzbinaService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.paramSub = this.route.queryParamMap.subscribe((params) => {
      const idStr = params.get('orderId');
      if (!idStr) {
        return;
      }
      const id = +idStr;
      if (Number.isNaN(id) || id <= 0) {
        return;
      }
      if (sessionStorage.getItem(this.guestTokenKey(id))) {
        void this.router.navigate(['/checkout/success'], {
          queryParams: { orderId: id },
          replaceUrl: true,
        });
      }
    });
  }

  ngOnDestroy(): void {
    this.alive = false;
    if (this.toastHideTimer) {
      clearTimeout(this.toastHideTimer);
    }
    this.paramSub?.unsubscribe();
  }

  ukloni(plocaId: number): void {
    this.cartService.ukloni(plocaId);
  }

  isprazni(): void {
    this.cartService.isprazni();
  }

  naruci(): void {
    if (!this.gostMejlIzgledaOk()) {
      this.prikaziToast(
        'Unesite ispravan e-mail za kontakt (za status porudžbine).',
        'warn'
      );
      return;
    }
    if (!this.adresaDostaveOk()) {
      this.prikaziToast(
        'Popunite ime, adresu, grad, poštanski broj, državu i telefon.',
        'warn'
      );
      return;
    }
    const stavke = this.cartService.stavkeKorpe();
    if (stavke.length === 0) {
      this.prikaziToast('Korpa je prazna.', 'warn');
      return;
    }

    this.ucitava = true;
    this.narudzbinaService
      .createGuest({
        gostEmail: this.gostEmail.trim(),
        stavke: stavke.map((s) => ({ plocaId: s.plocaId })),
        vidljiv: true,
        nacinPlacanja: this.nacinPlacanja,
        adresaIme: this.adresaIme.trim(),
        adresaUlica: this.adresaUlica.trim(),
        adresaGrad: this.adresaGrad.trim(),
        adresaPostanskiBroj: this.adresaPostanskiBroj.trim(),
        adresaDrzava: this.adresaDrzava.trim(),
        adresaTelefon: this.adresaTelefon.trim(),
      } as Narudzbina)
      .subscribe({
        next: (nar) => {
          if (nar.gostPristupniToken && nar.id != null) {
            sessionStorage.setItem(this.guestTokenKey(nar.id), nar.gostPristupniToken);
          }
          this.cartService.isprazni();
          this.ucitava = false;
          if (nar.id != null) {
            void this.router.navigate(['/checkout/success'], {
              queryParams: { orderId: nar.id },
              replaceUrl: true,
            });
          }
        },
        error: (err) => {
          this.ucitava = false;
          this.prikaziToast(
            this.porukaGreske(err, 'Greška pri kreiranju narudžbine.'),
            'error'
          );
        },
      });
  }

  private prikaziToast(poruka: string, tip: 'warn' | 'error'): void {
    if (this.toastHideTimer) {
      clearTimeout(this.toastHideTimer);
      this.toastHideTimer = undefined;
    }
    this.toastPoruka = poruka;
    this.toastTip = tip;
    this.toastVisible = false;
    this.cdr.detectChanges();

    queueMicrotask(() => {
      if (!this.alive) {
        return;
      }
      this.toastVisible = true;
      this.cdr.detectChanges();
      this.toastHideTimer = setTimeout(() => {
        if (!this.alive) {
          return;
        }
        this.toastVisible = false;
        this.toastHideTimer = undefined;
        this.cdr.detectChanges();
      }, tip === 'error' ? 3800 : 3500);
    });
  }

  slikaUrl(putanja: string): string {
    if (!putanja) {
      return '';
    }
    return 'http://localhost:8080/' + putanja;
  }

  private guestTokenKey(narudzbinaId: number): string {
    return `vinylverse_gost_narudzbina_${narudzbinaId}_token`;
  }

  private gostMejlIzgledaOk(): boolean {
    const e = this.gostEmail.trim();
    return e.length > 4 && e.includes('@') && e.includes('.');
  }

  private adresaDostaveOk(): boolean {
    return (
      this.adresaIme.trim().length > 0 &&
      this.adresaUlica.trim().length > 0 &&
      this.adresaGrad.trim().length > 0 &&
      this.adresaPostanskiBroj.trim().length > 0 &&
      this.adresaDrzava.trim().length > 0 &&
      this.adresaTelefon.trim().length > 0
    );
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
