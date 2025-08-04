import { Component, EventEmitter, Input, NgModule, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, FormsModule, NgModel, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FormaModel } from '../../../models/FormaModel';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-base-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule,FormsModule],
  templateUrl: './base-form.component.html',
  styleUrl: './base-form.component.css'
})
export class BaseFormComponent {
  constructor(private router: Router) {}

  @Input() postojecaSlikaUrl: string | null = null;

  @Input() formaModel: FormaModel | null = null;
  @Input() prikaziUploadSlike: boolean = false;

  @Output() submitEvent: EventEmitter<any> = new EventEmitter();
  @Output() cancelEvent = new EventEmitter<void>();

  forma: FormGroup = new FormGroup({});

  pesme: string[] = [];
novaPesma: string = '';

  slika: File | null = null;
  previewUrl: string | ArrayBuffer | null = null;

  kreirajFormu() {
    let grupa: any = {};
    if (this.formaModel) {
      for (let p of this.formaModel?.polja) {
        grupa[p.naziv] = new FormControl(p.podrazumevanaVrednost, p.validatori);
      }
    }
    this.forma = new FormGroup(grupa);
  }

ngOnChanges(changes: SimpleChanges): void {
  this.kreirajFormu();

  if (this.postojecaSlikaUrl && !this.previewUrl) {
    this.previewUrl = this.postojecaSlikaUrl;
  }

  const vrednost = this.forma.get('listaPesama')?.value;
if (vrednost && typeof vrednost === 'string') {
  this.pesme = vrednost.split(',').map(p => p.trim());
}
  }

  onSubmit() {
    if (this.forma.valid) {
      this.submitEvent.emit(this.forma.value);
    }
  }

  onCancel(): void {
    this.cancelEvent.emit();
  }

  compareFn = (a: any, b: any): boolean => {
    if (a && b && a.id && b.id) {
      return a.id === b.id;
    }
    return a === b;
  };

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files?.length) {
      this.slika = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(this.slika);
    }
  }

  getIzabranaSlika(): File | null {
    return this.slika;
  }

  ukloniSliku(): void {
  this.slika = null;
  this.previewUrl = null;
}

dodajPesmu(): void {
  if (this.novaPesma.trim()) {
    this.pesme.push(this.novaPesma.trim());
    this.novaPesma = '';
    this.forma.get('listaPesama')?.setValue(this.pesme.join(','));
  }
}

ukloniPesmu(index: number): void {
  this.pesme.splice(index, 1);
  this.forma.get('listaPesama')?.setValue(this.pesme.join(','));
}

}
