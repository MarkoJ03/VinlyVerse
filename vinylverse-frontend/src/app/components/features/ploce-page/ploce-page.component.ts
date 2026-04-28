import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

import { Ploca } from '../../../models/Ploca';
import { PlocaService } from '../../../services/ploca.service';
import { PlocaCardComponent } from '../../shared/ploca-card/ploca-card.component';
import { ZanrService } from '../../../services/zanr.service';
import { Zanr } from '../../../models/Zanr';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-ploce-page',
  standalone: true,
  imports: [CommonModule, FormsModule, PlocaCardComponent],
  templateUrl: './ploce-page.component.html',
  styleUrl: './ploce-page.component.css'
})
export class PlocePageComponent implements OnInit, OnDestroy {
  allPloce: Ploca[] = [];
  ploce: Ploca[] = [];
  filteredPloce: Ploca[] = [];

  page = 0;
  pageSize = 15;
  hasNext = true;

  searchTerm: string = '';
  sortOption: string = '';

  private sviZanrovi: Zanr[] = [];
  zanrovi: Zanr[] = [];
  zanrId: number | '' = '';

  private searchSubject = new Subject<string>();

  showToast = false;
  toastPoruka = '';
  toastWarn = false;
  private toastHideTimer?: ReturnType<typeof setTimeout>;
  private alive = true;

  constructor(
    private plocaService: PlocaService,
    private zanrService: ZanrService,
    private cartService: CartService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.ucitajPaginiranePloce();

    // Debounced global search (samo kad je izabran "Svi žanrovi").
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((term) => {
          const trimmed = term.trim();

          // Kad korisnik izabere konkretan žanr, zadržavamo postojeće lokalno filtriranje.
          if (this.zanrId !== '') {
            this.searchTerm = term;
            this.page = 0;
            this.applyFilter();
            return of(this.ploce);
          }

          this.searchTerm = term;
          this.page = 0;

          if (trimmed === '') {
            return this.plocaService.getPaginirane(this.page, this.pageSize);
          }

          return this.plocaService.searchPloce(trimmed, this.page, this.pageSize);
        })
      )
      .subscribe((data) => {
        if (this.zanrId !== '') return; // genre mode handled locally above

        this.allPloce = data;
        this.ploce = data.slice();
        this.applyFilter();
        this.recalcAvailableGenres();
        this.hasNext = data.length === this.pageSize;
      });

    this.zanrService.getAll().subscribe(data => {
      this.sviZanrovi = data;
      this.recalcAvailableGenres();
    });
  }

  ngOnDestroy(): void {
    this.alive = false;
    if (this.toastHideTimer) {
      clearTimeout(this.toastHideTimer);
    }
  }

  private recalcAvailableGenres(): void {
    const ids = new Set<number>(
      this.allPloce
        .map(p => p.zanr?.id)
        .filter((v): v is number => typeof v === 'number')
    );
    this.zanrovi = this.sviZanrovi.filter(z => ids.has(z.id));
  }

  private ucitajPaginiranePloce(): void {
    if (this.zanrId !== '') return; 

    const term = this.searchTerm.trim();

    const obs =
      term === ''
        ? this.plocaService.getPaginirane(this.page, this.pageSize)
        : this.plocaService.searchPloce(term, this.page, this.pageSize);

    obs
      .subscribe({
        next: data => {
          this.allPloce = data;
          this.ploce = data.slice();
          this.applyFilter();
          this.recalcAvailableGenres();
          this.hasNext = data.length === this.pageSize;
        },
      });
  }

  onZanrChange(): void {
    if (this.zanrId === '') {
      this.page = 0;
      this.ucitajPaginiranePloce();
    } else {
      this.plocaService.getByZanrId(+this.zanrId).subscribe(data => {
        this.ploce = data;
        this.allPloce = data;
        this.applyFilter();
        this.hasNext = false;
      });
    }
  }

  applyFilter(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredPloce = this.ploce.filter(ploca =>
      ploca.proizvod.naziv.toLowerCase().includes(term)
    );
    this.applySort();
  }

  applySort(): void {
    switch (this.sortOption) {
      case 'naziv':
        this.filteredPloce.sort((a, b) =>
          a.proizvod.naziv.localeCompare(b.proizvod.naziv)
        );
        break;
      case 'cena':
        this.filteredPloce.sort((a, b) => a.proizvod.cena - b.proizvod.cena);
        break;
      case 'cenaDesc':
        this.filteredPloce.sort((a, b) => b.proizvod.cena - a.proizvod.cena);
        break;
    }
  }

  generateSlug(naziv: string, izdavackaKuca: string): string {
    return (naziv + '-' + izdavackaKuca)
      .toLowerCase()
      .replace(/\s+/g, '-');
  }

  prethodnaStranica(): void {
    if (this.zanrId !== '') return;
    if (this.page <= 0) return;
    this.page--;
    this.ucitajPaginiranePloce();
  }

  sledecaStranica(): void {
    if (this.zanrId !== '') return;
    if (!this.hasNext) return;
    this.page++;
    this.ucitajPaginiranePloce();
  }

  onSearchTermChange(term: string): void {
    this.searchTerm = term;
    this.page = 0;

    if (this.zanrId !== '') {

      this.applyFilter();
      return;
    }

    this.searchSubject.next(term);
  }

  trackByPlocaId(index: number, ploca: Ploca): number {
    return ploca.id!;
  }

  dodajUKorpu(ploca: Ploca): void {
    const dodato = this.cartService.dodaj(ploca);
    if (this.toastHideTimer) {
      clearTimeout(this.toastHideTimer);
      this.toastHideTimer = undefined;
    }

    this.toastWarn = !dodato;
    this.toastPoruka = dodato
      ? 'Ploča dodata u korpu!'
      : 'Ova ploča je već u korpi.';

this.showToast = false;
    this.cdr.detectChanges();

    queueMicrotask(() => {
      if (!this.alive) {
        return;
      }
      this.showToast = true;
      this.cdr.detectChanges();
      this.toastHideTimer = setTimeout(() => {
        if (!this.alive) {
          return;
        }
        this.showToast = false;
        this.toastHideTimer = undefined;
        this.cdr.detectChanges();
      }, 3000);
    });
  }
}
