import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-base-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './base-table.component.html',
  styleUrl: './base-table.component.css'
})
export class BaseTableComponent<T extends { [key: string]: any }> {
  @Input() data: T[] = [];
  @Input() displayedColumns: string[] = [];

  @Output() edit = new EventEmitter<T>();
  @Output() delete = new EventEmitter<number>();
  @Output() details = new EventEmitter<number>();

  onEdit(item: T) {
    this.edit.emit(item);
  }

  onDelete(id: number) {
    this.delete.emit(id);
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
}
