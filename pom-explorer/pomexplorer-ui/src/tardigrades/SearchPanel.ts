"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface SearchPanelDto {
    _root?: string;
    input?: any;
"@input"?: any;

}

export class SearchPanel {
    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(SearchPanel.loaded)
            return;
        SearchPanel.loaded = true;

        

        tardigradeEngine.addTemplate("SearchPanel", new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label"}, [new ElementNode("input", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "searchBox"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "searchBox"}, [new TextNode("Project search...")])]), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-button mdl-button--icon"}, [new ElementNode(null, <Cardinal>0, [""], "i", {"class": "material-icons"}, [new TextNode("search")])])]));
    }

    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: SearchPanelDto): string {
        SearchPanel.ensureLoaded();

        return tardigradeEngine.buildHtml("SearchPanel", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:SearchPanelDto): HTMLElement {
        return createElement(SearchPanel.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:SearchPanelDto): SearchPanel {
        let element = SearchPanel.element(dto);
        return new SearchPanel(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): SearchPanel {
        return new SearchPanel(element);
    }

    /** This constructor should not be called by your application ! */
    constructor(private rootElement: HTMLElement) {}

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
return <HTMLInputElement>tardigradeEngine.getPoint(this.rootElement, "SearchPanel", { "input": 0 });
}
/** Returns true if the part named 'input' with id 'input' was hit */
                inputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "SearchPanel", hitTest);
                        return (location != null && ("input" in location));
                        }
}