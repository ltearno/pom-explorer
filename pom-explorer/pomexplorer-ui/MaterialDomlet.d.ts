import { Domlet } from "Domlet";
export declare class MaterialDomlet extends Domlet {
    constructor(template: string, points: {
        [key: string]: number[];
    });
    htmlElement(): HTMLElement;
    initMaterialElement(e: HTMLElement): void;
}
