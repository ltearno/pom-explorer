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
    input?: BaseDto;
    _root?: BaseDto;
}


export class SearchPanel {
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: _RootDto): string {
        SearchPanel.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("SearchPanel", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto: _RootDto): HTMLElement {
        return tardigrade.createElement(SearchPanel.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto: _RootDto): SearchPanel {
        let element = SearchPanel.element(dto);
        return new SearchPanel(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: Element): SearchPanel {
        return new SearchPanel(element);
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

    /** Returns the html element corresponding to the 'input' point */
input(): HTMLInputElement {
return <HTMLInputElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "SearchPanel", { "input": 0 });
}
/** Returns true if the part named 'input' with id 'input' was hit */
                inputHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "SearchPanel", hitTest);
                        return (location != null && ("input" in location));
                        }

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(SearchPanel.loaded)
            return;
        SearchPanel.loaded = true;

        

        tardigrade.tardigradeEngine.addTemplate("SearchPanel", {e:[null, 0, [""], "div", {}, [{e:[null, 0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label"}, [{e:["input", 0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "searchBox"}, []]}, {e:[null, 0, [""], "label", {"class": "mdl-textfield__label", "for": "searchBox"}, ["Project search..."]]}]]}, {e:[null, 0, [""], "div", {"class": "mdl-button mdl-button--icon"}, [{e:[null, 0, [""], "i", {"class": "material-icons"}, ["search"]]}]]}]]});
    }
}