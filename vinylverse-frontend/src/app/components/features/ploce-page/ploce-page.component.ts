import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Ploca } from '../../../models/Ploca';
import { PlocaService } from '../../../services/ploca.service';
import { PlocaCardComponent } from '../../shared/ploca-card/ploca-card.component';
import { ZanrService } from '../../../services/zanr.service';
import { Zanr } from '../../../models/Zanr';

@Component({
  selector: 'app-ploce-page',
  standalone: true,
  imports: [CommonModule, FormsModule, PlocaCardComponent],
  templateUrl: './ploce-page.component.html',
  styleUrl: './ploce-page.component.css'
})
export class PlocePageComponent implements OnInit {
  ploce: Ploca[] = [];
  filteredPloce: Ploca[] = [];

  searchTerm: string = '';
  sortOption: string = '';

  zanrovi: Zanr[] = [];
  zanrId: number | '' = '';

  constructor(
    private plocaService: PlocaService,
    private zanrService: ZanrService
  ) {}

  ngOnInit(): void {
    this.ucitajSvePloce();
    this.zanrService.getAll().subscribe(data => {
      this.zanrovi = data;
    });
  }

  ucitajSvePloce(): void {
    this.plocaService.getAll().subscribe(data => {
      this.ploce = data;
      this.applyFilter();
    });
  }

  onZanrChange(): void {
    if (this.zanrId === '') {
      this.ucitajSvePloce(); // svi žanrovi
    } else {
      this.plocaService.getByZanrId(+this.zanrId).subscribe(data => {
        this.ploce = data;
        this.applyFilter();
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

  trackByPlocaId(index: number, ploca: Ploca): number {
    return ploca.id!;
  }
}
