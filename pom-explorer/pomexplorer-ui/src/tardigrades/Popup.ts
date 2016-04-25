"use strict";

declare var tardigrade;



export type BaseDto = string | {
    _?: string; // node's text content
    $?: { [attributeName:string]: string | number }; // node's attribute values
}

/**
 * Template's DTO interface.
 * Used to create new template instances */

export type _RootDto = string | {
    _?: string; // node's text content
    $?: { [attributeName:string]: string | number }; // node's attribute values
    content?: BaseDto;
    _root?: BaseDto;
}


export class Popup {
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: _RootDto): string {
        Popup.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("Popup", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto: _RootDto): HTMLElement {
        return tardigrade.createElement(Popup.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto: _RootDto): Popup {
        let element = Popup.element(dto);
        return new Popup(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: Element): Popup {
        return new Popup(element);
    }

    private rootElement: HTMLElement;

    /** This constructor should not be called by your application ! */
    constructor(rootElement: Element) {
        this.rootElement = <HTMLElement>rootElement;
    }

    /** Returns the root element of this template */
    rootHtmlElement(): HTMLElement { return this.rootElement; }

    /** Sets the user data associated with the root element of the template
     * @return The previous data that was associated, or undefined
     */
    setUserData(data:any): any {
        let old = (this.rootElement as any)._tardigradeUserData || undefined;
        (this.rootElement as any)._tardigradeUserData = data;
        return old;
    }

    /** Returns the user data associated with the root element of the template */
    getUserData():any {
        return (this.rootElement as any)._tardigradeUserData || undefined;
    }

    /** Returns the html element corresponding to the 'content' point */
content(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "Popup", { "content": 0 });
}
/** Returns true if the part named 'content' with id 'content' was hit */
                contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "Popup", hitTest);
                        return (location != null && ("content" in location));
                        }

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(Popup.loaded)
            return;
        Popup.loaded = true;

        

        tardigrade.tardigradeEngine.addTemplate("Popup", {e:["content", 0, [""], "div", {"class": "Popup"}, []]});
    }
}