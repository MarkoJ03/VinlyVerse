import { Proizvod } from "./Proizvod";
import { Zanr } from "./Zanr";

export interface Ploca{
    id:number;
    proizvod: Proizvod;
    listaPesama: string;
    brend: string;
    izdavackaKuca: string;
    zanr: Zanr;
    vidljiv: boolean;
}