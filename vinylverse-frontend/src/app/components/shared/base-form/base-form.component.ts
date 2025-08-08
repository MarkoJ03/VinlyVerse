import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FormaModel } from '../../../models/FormaModel';
import { CommonModule } from '@angular/common';
import { ImageCropperComponent, ImageCroppedEvent } from 'ngx-image-cropper';

@Component({
  selector: 'app-base-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule, ImageCropperComponent],
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
  novaPesma = '';

  slika: File | null = null;
  croppedFile: File | null = null;
  previewUrl: string | null = null;
  imageChangedEvent: any = null;

  kreirajFormu() {
    const grupa: any = {};
    if (this.formaModel) {
      for (const p of this.formaModel.polja) {
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
    if (a && b && a.id && b.id) return a.id === b.id;
    return a === b;
  };

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files?.length) {
      this.slika = input.files[0];
      this.imageChangedEvent = event;
    }
  }

  onImageCropped(event: ImageCroppedEvent): void {
    if (event.blob) {
      this.croppedFile = new File([event.blob], this.slika?.name || 'cropped.png', { type: event.blob.type });
      const reader = new FileReader();
      reader.onload = () => (this.previewUrl = reader.result as string);
      reader.readAsDataURL(this.croppedFile);
      return;
    }
    if (event.base64) {
      this.previewUrl = event.base64;
      this.croppedFile = this.base64ToFile(event.base64, this.slika?.name || 'cropped.png');
    }
  }

  private base64ToFile(base64: string, filename: string): File {
    const arr = base64.split(',');
    const mime = arr[0].match(/:(.*?);/)?.[1] || 'image/png';
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8 = new Uint8Array(n);
    while (n--) u8[n] = bstr.charCodeAt(n);
    return new File([u8], filename, { type: mime });
  }

  getIzabranaSlika(): File | null {
    return this.croppedFile ?? this.slika;
  }

  ukloniSliku(): void {
    this.slika = null;
    this.croppedFile = null;
    this.previewUrl = null;
    this.imageChangedEvent = null;
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
