import { Injectable } from '@angular/core';
import { Proizvod } from '../models/Proizvod';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { Observable } from 'rxjs/internal/Observable';

@Injectable({
  providedIn: 'root'
})
export class ProizvodService extends BaseService<Proizvod> {
  constructor(http: HttpClient) {
    super(http);
    this.setUrl('http://localhost:8080/api/proizvod');
  }

  uploadSlika(file: File): Observable<string> {
  const formData = new FormData();
  formData.append('file', file);

  return this.http.post('/api/slike/upload', formData, { responseType: 'text' });
}
}