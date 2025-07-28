import { Component } from '@angular/core';
import { HeaderComponent } from "../../core/header/header.component";
import { FooterComponent } from '../../core/footer/footer.component';
import { PlocaCardComponent } from '../../shared/ploca-card/ploca-card.component';
import { CommonModule } from '@angular/common';
import { PlocaService } from '../../../services/ploca.service';
import { Ploca } from '../../../models/Ploca';

@Component({
  selector: 'app-home-page',
  imports: [PlocaCardComponent,CommonModule],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent {

 ploce: Ploca[] = [];
  page = 0;
  size = 3;

  constructor(private plocaService: PlocaService) {}

  ngOnInit(): void {
    this.ucitajPaginirane();
  }

  ucitajPaginirane(): void {
    this.plocaService.getPaginirane(this.page, this.size).subscribe(data => {
      this.ploce = data;
    });
  }

  sledecaStrana(): void {
    this.page++;
    this.ucitajPaginirane();
  }

  prethodnaStrana(): void {
    if (this.page > 0) {
      this.page--;
      this.ucitajPaginirane();
    }
  }
}


