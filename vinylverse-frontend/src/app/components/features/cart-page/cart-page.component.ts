import { Component, OnDestroy, OnInit } from '@angular/core';
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
  greska: string | null = null;
  ucitava = false;

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
    private route: ActivatedRoute
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
    this.paramSub?.unsubscribe();
  }

  ukloni(plocaId: number): void {
    this.cartService.ukloni(plocaId);
    this.greska = null;
  }

  isprazni(): void {
    this.cartService.isprazni();
    this.greska = null;
  }

  naruci(): void {
    if (!this.gostMejlIzgledaOk()) {
      this.greska = 'Unesite ispravan e-mail za kontakt (za status porudžbine).';
      return;
    }
    if (!this.adresaDostaveOk()) {
      this.greska = 'Popunite ime, adresu, grad, poštanski broj, državu i telefon.';
      return;
    }
    const stavke = this.cartService.stavkeKorpe();
    if (stavke.length === 0) {
      return;
    }

    this.ucitava = true;
    this.greska = null;
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
          this.greska = this.porukaGreske(err, 'Greška pri kreiranju narudžbine.');
        },
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
