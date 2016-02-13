"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface PopupDto {
    _root?: string;
    content?: any;
"@content"?: any;

}

export class Popup {
    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(Popup.loaded)
            return;
        Popup.loaded = true;

        

        tardigradeEngine.addTemplate("Popup", new ElementNode("content", <Cardinal>0, [""], "div", {"class": "Popup"}, []));
    }

    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: PopupDto): string {
        Popup.ensureLoaded();

        return tardigradeEngine.buildHtml("Popup", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:PopupDto): HTMLElement {
        return createElement(Popup.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:PopupDto): Popup {
        let element = Popup.element(dto);
        return new Popup(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): Popup {
        return new Popup(element);
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

    /** Returns the html element corresponding to the 'content' point */
content(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Popup", { "content": 0 });
}
/** Returns true if the part named 'content' with id 'content' was hit */
                contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Popup", hitTest);
                        return (location != null && ("content" in location));
                        }
}