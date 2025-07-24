import { Injectable } from '@angular/core';
import { Zanr } from '../models/Zanr';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class ZanrService extends BaseService<Zanr> {
  constructor(http: HttpClient) {
    super(http);
    this.setUrl('http://localhost:8080/api/zanr');
  }
}