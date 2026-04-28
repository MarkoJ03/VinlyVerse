import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Ploca } from '../../../models/Ploca';
import { PlocaService } from '../../../services/ploca.service';
import { CommonModule } from '@angular/common';
import { PlocaCardComponent } from '../../shared/ploca-card/ploca-card.component';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-ploca-details',
  imports: [CommonModule,PlocaCardComponent],
  templateUrl: './ploca-details.component.html',
  styleUrl: './ploca-details.component.css'
})
export class PlocaDetailsComponent implements OnInit, OnDestroy {

ploca: any;

  private toastHideTimer?: ReturnType<typeof setTimeout>;
  private alive = true;

  constructor(
    private route: ActivatedRoute,
    private ploceService: PlocaService,
    private cartService: CartService,
    private cdr: ChangeDetectorRef
  ) {}

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

  ngOnDestroy(): void {
    this.alive = false;
    if (this.toastHideTimer) {
      clearTimeout(this.toastHideTimer);
    }
  }

showToast = false;

dodajUKorpu() {
  if (!this.ploca) {
    return;
  }
  const ploca: Ploca = this.ploca as Ploca;
  this.cartService.dodaj(ploca);

  if (this.toastHideTimer) {
    clearTimeout(this.toastHideTimer);
    this.toastHideTimer = undefined;
  }

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

getSlikaUrl(putanja: string): string {
  return 'http://localhost:8080/' + putanja;
}

}
