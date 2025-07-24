import { Injectable } from '@angular/core';
import { Proizvod } from '../models/Proizvod';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class ProizvodService extends BaseService<Proizvod> {
  constructor(http: HttpClient) {
    super(http);
    this.setUrl('http://localhost:8080/api/proizvod');
  }
}