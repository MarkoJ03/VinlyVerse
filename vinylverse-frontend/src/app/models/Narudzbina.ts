import { StavkaNarudzbine } from './StavkaNarudzbine';

export type NacinPlacanjaKod = 'PAYPAL' | 'POUZEC';

export interface Narudzbina {
  id?: number;
  korisnikId?: number;
  korisnikEmail?: string;
  gostEmail?: string;

  gostPristupniToken?: string;
  stavke?: StavkaNarudzbine[];
  status?: string;
  ukupanIznos?: number;
  datumKreiranja?: string;
  paymentProvider?: string | null;
  providerOrderId?: string | null;
  paymentCapturedAt?: string | null;
  nacinPlacanja?: NacinPlacanjaKod | string | null;
  adresaIme?: string;
  adresaUlica?: string;
  adresaGrad?: string;
  adresaPostanskiBroj?: string;
  adresaDrzava?: string;
  adresaTelefon?: string;
  vidljiv?: boolean;
}
