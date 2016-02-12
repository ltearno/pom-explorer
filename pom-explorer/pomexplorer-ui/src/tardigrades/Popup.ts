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

    static ensureLoaded() {
        if(Popup.loaded)
            return;
        Popup.loaded = true;

        

        tardigradeEngine.addTemplate("Popup", new ElementNode("content", <Cardinal>0, [""], "div", {"class": "Popup"}, []));
    }

    static html(dto: PopupDto): string {
        Popup.ensureLoaded();

        return tardigradeEngine.buildHtml("Popup", dto);
    }

    static element(dto:PopupDto): HTMLElement {
        return createElement(Popup.html(dto));
    }

    static create(dto:PopupDto): Popup {
        let element = Popup.element(dto);
        return new Popup(element);
    }

    static of(element: HTMLElement): Popup {
        return new Popup(element);
    }

    constructor(private rootElement: HTMLElement) {}

    rootHtmlElement(): HTMLElement { return this.rootElement; }

    setUserData(data:any): any {
        let old = (this.rootElement as any)._tardigradeUserData || undefined;
        (this.rootElement as any)._tardigradeUserData = data;
        return old;
    }

    getUserData():any {
        return (this.rootElement as any)._tardigradeUserData || undefined;
    }

    content(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Popup", { "content": 0 });
}
contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Popup", hitTest);
                        return (location != null && ("content" in location));
                        }
}