import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlocaService } from '../../../services/ploca.service';
import { Ploca } from '../../../models/Ploca';
import { PlocaCardComponent } from '../../shared/ploca-card/ploca-card.component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [PlocaCardComponent, CommonModule],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit {
  ploce: Ploca[] = [];
  startIndex: number = 0;
  visibleCount = 3;

  constructor(private plocaService: PlocaService) {}

  ngOnInit(): void {
    this.plocaService.getAll().subscribe(data => {
      this.ploce = data;
    });
  }

  get trackTransform(): string {
    return `translateX(-${(100 / this.visibleCount) * this.startIndex}%)`;
  }

  sledeca(): void {
    if (this.startIndex + this.visibleCount < this.ploce.length) {
      this.startIndex++;
    }
  }

  prethodna(): void {
    if (this.startIndex > 0) {
      this.startIndex--;
    }
  }
}
