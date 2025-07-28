import { Component } from '@angular/core';
import { HeaderComponent } from "../../core/header/header.component";
import { FooterComponent } from '../../core/footer/footer.component';
import { PlocaCardComponent } from '../../shared/ploca-card/ploca-card.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home-page',
  imports: [HeaderComponent,FooterComponent,PlocaCardComponent,CommonModule],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent {
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
}

