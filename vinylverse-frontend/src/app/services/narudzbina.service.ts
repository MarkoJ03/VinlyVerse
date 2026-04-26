import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from './base.service';
import { Narudzbina } from '../models/Narudzbina';
import { PayPalCaptureResponse, PayPalCreateOrderResponse } from '../models/PayPalDto';

@Injectable({
  providedIn: 'root',
})
export class NarudzbinaService extends BaseService<Narudzbina> {
  private readonly base = 'http://localhost:8080/api/narudzbina';

  constructor(http: HttpClient) {
    super(http);
    this.setUrl(this.base);
  }

  private gostHederi(gostPristupniToken: string | null | undefined): { headers: HttpHeaders } {
    if (!gostPristupniToken) {
      return { headers: new HttpHeaders() };
    }
    return {
      headers: new HttpHeaders().set('X-Order-Token', gostPristupniToken),
    };
  }

  createGuest(narudzbina: Narudzbina): Observable<Narudzbina> {
    return this.http.post<Narudzbina>(`${this.base}/guest`, narudzbina);
  }

  getGuestPregled(id: number, token: string): Observable<Narudzbina> {
    return this.http.get<Narudzbina>(`${this.base}/guest/${id}`, { params: { token } });
  }

  getMoje(): Observable<Narudzbina[]> {
    return this.http.get<Narudzbina[]>(`${this.base}/moje`);
  }

  payMock(narudzbinaId: number, gostToken?: string | null): Observable<Narudzbina> {
    return this.http.post<Narudzbina>(
      `${this.base}/${narudzbinaId}/pay-mock`,
      {},
      this.gostHederi(gostToken)
    );
  }

  createPayPalOrder(
    narudzbinaId: number,
    gostToken?: string | null
  ): Observable<PayPalCreateOrderResponse> {
    return this.http.post<PayPalCreateOrderResponse>(
      `${this.base}/${narudzbinaId}/paypal/create-order`,
      {},
      this.gostHederi(gostToken)
    );
  }

  capturePayPal(
    narudzbinaId: number,
    gostToken?: string | null
  ): Observable<PayPalCaptureResponse> {
    return this.http.post<PayPalCaptureResponse>(
      `${this.base}/${narudzbinaId}/paypal/capture`,
      {},
      this.gostHederi(gostToken)
    );
  }
}
