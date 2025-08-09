import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, output, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-base-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './base-table.component.html',
  styleUrl: './base-table.component.css'
})
export class BaseTableComponent <T extends { [key: string]: any }>implements OnChanges {
  @Input() data: T[] = [];
  @Input() displayedColumns: string[] = [];

  @Output() edit = new EventEmitter<T>();
  @Output() delete = new EventEmitter<number>();
  @Output() details = new EventEmitter<number>();
@Output() add = new EventEmitter<void>();

constructor(private router: Router) {}
  searchText: string = '';
  filteredData: any[] = [];

  ngOnChanges(): void {
    this.applyFilter();
  }

  applyFilter(): void {
    const term = this.searchText.toLowerCase();
    this.filteredData = this.data.filter(item =>
      this.displayedColumns.some(col => {
        const value = this.getValueByPath(item, col);
        console.log("ee");
        return String(value).toLowerCase().includes(term);
      })
    );
  }

  onEdit(item: T) {
    this.edit.emit(item);
  }

onDelete(id: number) {
  Swal.fire({
    title: 'Brisanje zapisa',
    text: 'Da li ste sigurni da želite da obrišete ovaj zapis?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Da, obriši',
    cancelButtonText: 'Otkaži'
  }).then((result) => {
    if (result.isConfirmed) {
      this.delete.emit(id);
      Swal.fire('Obrisano!', 'Zapis je uspešno obrisan.', 'success');
    }
  });
}

  isObject(value: any): boolean {
    return typeof value === 'object' && value !== null && !Array.isArray(value);
  }

  extractDisplayValue(value: any): string {
    if (!value) return '-';
    return value.ime || value.naziv || value.id || '[objekat]';
  }

  getValueByPath(obj: any, path: string): any {
    return path.split('.').reduce((acc, part) => acc?.[part], obj);
  }

  generateSlug(naziv: string, izdavackaKuca: string): string {
  return (naziv + '-' + izdavackaKuca)
    .toLowerCase()
    .replace(/\s+/g, '-')
}

goToDetails(item: any): void {
  const slug = this.generateSlug(item.proizvod?.naziv, item.izdavackaKuca);
  this.router.navigate(['/detalji', item.id, slug]);
}

goToAdd(): void {
  this.add.emit();
}

isImagePath(value: any): boolean {
  if (typeof value !== 'string') return false;
  return /\.(jpg|jpeg|png|gif|webp)$/i.test(value);
}

}
