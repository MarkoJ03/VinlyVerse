import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { StavkaNarudzbine } from '../models/StavkaNarudzbine';

@Injectable({
  providedIn: 'root',
})
export class StavkaNarudzbineService extends BaseService<StavkaNarudzbine> {
  constructor(http: HttpClient) {
    super(http);
    this.setUrl('http://localhost:8080/api/stavka-narudzbine');
  }
}
