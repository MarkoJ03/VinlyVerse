import { Injectable } from '@angular/core';
import { PravoPristupa } from '../models/PravoPristupa';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class PravoPristupaService extends BaseService<PravoPristupa> {
  constructor(http: HttpClient) {
    super(http);
    this.setUrl('http://localhost:8080/api/pravoPristupa');
  }
}