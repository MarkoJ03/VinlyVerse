import { Component, ElementRef, ViewChild } from '@angular/core';
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
export class PlocaDetailsComponent {

ploca: any;

  constructor(private route: ActivatedRoute, private ploceService: PlocaService) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.ploceService.getById(+id).subscribe(data => {
        this.ploca = data;
      });
    }
  }

showToast = false;

dodajUKorpu() {
  console.log('Dodato u korpu:', this.ploca.proizvod.naziv);

  // ovde ubaciš poziv servisa ako imaš

  this.showToast = true;

  setTimeout(() => {
    this.showToast = false;
  }, 3000); // toast nestaje nakon 3 sekunde
}


@ViewChild('carousel', { static: false }) carousel!: ElementRef;

 preporucenePloce = [
    {
      naslov: 'John Coltrane – Blue Train',
      izvodjac: 'John Coltrane',
      godina: 1957,
      cena: 24.99,
      slika: 'https://i.imgur.com/ZxM9Rgt.jpeg'
    },
    {
      naslov: 'Billie Holiday – Lady in Satin',
      izvodjac: 'Billie Holiday',
      godina: 1958,
      cena: 26.50,
      slika: 'https://i.imgur.com/I5t1nSv.jpeg'
    },
    {
      naslov: 'Miles Davis – Kind of Blue',
      izvodjac: 'Miles Davis',
      godina: 1959,
      cena: 27.90,
      slika: 'https://i.imgur.com/tQpT8zq.jpeg'
    }
  ];

scrollLeft() {
  this.carousel.nativeElement.scrollBy({ left: -300, behavior: 'smooth' });
}

scrollRight() {
  this.carousel.nativeElement.scrollBy({ left: 300, behavior: 'smooth' });
}
}
