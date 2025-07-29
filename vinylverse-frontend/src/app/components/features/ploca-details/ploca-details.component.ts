import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Ploca } from '../../../models/Ploca';
import { PlocaService } from '../../../services/ploca.service';
import { CommonModule } from '@angular/common';
import { PlocaCardComponent } from '../../shared/ploca-card/ploca-card.component';

@Component({
  selector: 'app-ploca-details',
  imports: [CommonModule,PlocaCardComponent],
  templateUrl: './ploca-details.component.html',
  styleUrl: './ploca-details.component.css'
})
export class PlocaDetailsComponent implements OnInit{

ploca: any;

  constructor(private route: ActivatedRoute, private ploceService: PlocaService) {}

  ngOnInit(): void {
    const id = this.route.params.subscribe(params => {
  const id = +params['id'];
  if (id) {
    this.ploceService.getById(id).subscribe(data => {
      this.ploca = data;
    });
  }
});

      this.ploceService.getAll().subscribe(data => {
      this.ploce = data;
    });
    
  }

showToast = false;

dodajUKorpu() {
  console.log('Dodato u korpu:', this.ploca.proizvod.naziv);



  this.showToast = true;

  setTimeout(() => {
    this.showToast = false;
  }, 3000); 
}


 ploce: Ploca[] = [];
  startIndex: number = 0;
  visibleCount = 3;





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

  generateSlug(naziv: string, izdavackaKuca: string): string {
  return (naziv + '-' + izdavackaKuca)
    .toLowerCase()
    .replace(/\s+/g, '-')

}
}
