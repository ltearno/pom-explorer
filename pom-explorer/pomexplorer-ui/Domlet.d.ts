/**
 * TODO : mustache should not be used here, and a creation DTO should be expected to
 * contain fields for each of the (terminal) points
 */
export declare class Domlet {
    template: string;
    points: {
        [key: string]: number[];
    };
    constructor(template: string, points: {
        [key: string]: number[];
    });
    html(mustacheDto?: any): string;
    htmlElement(mustacheDto?: any): HTMLElement;
    point(name: string, domletElement: HTMLElement): HTMLElement;
    getComingChild(p: HTMLElement, element: HTMLElement, domletElement: HTMLElement): HTMLElement;
    indexOf(point: string, element: HTMLElement, domletElement: HTMLElement): any;
    private pointInternal(list, domletElement);
}
