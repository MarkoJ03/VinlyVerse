import { Injectable } from '@angular/core';
import { Ploca } from '../models/Ploca';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class PlocaService extends BaseService<Ploca> {
  constructor(http: HttpClient) {
    super(http);
    this.setUrl('http://localhost:8080/api/ploca');
  }
getPaginirane(page: number, size: number) {
  return this.http.get<Ploca[]>(`http://localhost:8080/api/ploca/paginacija?page=${page}&size=${size}`);
}

getNasumicne(broj: number) {
  return this.http.get<Ploca[]>(`http://localhost:8080/api/ploca/nasumicno?broj=${broj}`);
}
  
}