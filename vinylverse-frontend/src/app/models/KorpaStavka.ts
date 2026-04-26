import { Ploca } from './Ploca';


export interface KorpaStavka {
  plocaId: number;
  naziv: string;
  cena: number;
  slikaPutanja: string;
}


export function korpaStavkaIzPloce(ploca: Ploca): KorpaStavka | null {
  if (ploca.id == null) {
    return null;
  }
  return {
    plocaId: ploca.id,
    naziv: ploca.proizvod.naziv,
    cena: ploca.proizvod.cena,
    slikaPutanja: ploca.proizvod.slikaPutanja ?? '',
  };
}
