import { Proizvod } from "./Proizvod";
import { Zanr } from "./Zanr";

export interface Ploca{
    id?:number;
    proizvod: Proizvod;
    listaPesama: string;
    brend: string;
    izdavackaKuca: string;
    godinaIzdanja: string;
    zanr: Zanr;
    vidljiv: boolean;
}