import { Injectable, computed, signal } from '@angular/core';
import { Ploca } from '../models/Ploca';
import { KorpaStavka, korpaStavkaIzPloce } from '../models/KorpaStavka';

const STORAGE_KEY = 'vinylverse_korpa';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private readonly stavke = signal<KorpaStavka[]>(this.ucitajIzStoragea());

  readonly stavkeKorpe = this.stavke.asReadonly();
  readonly brojStavki = computed(() => this.stavke().length);

  readonly ukupno = computed(() =>
    this.stavke().reduce((z, s) => z + s.cena, 0)
  );

  dodaj(ploca: Ploca): boolean {
    const stavka = korpaStavkaIzPloce(ploca);
    if (!stavka) {
      return false;
    }
    const trenutno = this.stavke();
    if (trenutno.some((s) => s.plocaId === stavka.plocaId)) {
      return false;
    }
    const novo = [...trenutno, stavka];
    this.stavke.set(novo);
    this.sacuvaj(novo);
    return true;
  }

  ukloni(plocaId: number): void {
    const novo = this.stavke().filter((s) => s.plocaId !== plocaId);
    this.stavke.set(novo);
    this.sacuvaj(novo);
  }

  isprazni(): void {
    this.stavke.set([]);
    this.sacuvaj([]);
  }

  sadrzi(plocaId: number): boolean {
    return this.stavke().some((s) => s.plocaId === plocaId);
  }

  private ucitajIzStoragea(): KorpaStavka[] {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return [];
      }
      const parsed = JSON.parse(raw) as unknown;
      return Array.isArray(parsed) ? (parsed as KorpaStavka[]) : [];
    } catch {
      return [];
    }
  }

  private sacuvaj(stavke: KorpaStavka[]): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(stavke));
  }
}
