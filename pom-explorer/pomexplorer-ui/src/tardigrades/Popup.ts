"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface PopupTemplateDto {
    _root?: string;
    content?: any;
"@content"?: any;

}

export interface PopupTemplateElement {
    _root(): HTMLElement;
    // returns the previous data
    setUserData(data:any):any;
    getUserData():any;
    content(): HTMLDivElement;
contentHit(hitTest:HTMLElement): boolean;
}

class PopupTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("Popup", new ElementNode("content", <Cardinal>0, [""], "div", {"class": "Popup"}, []));
    }

    buildHtml(dto: PopupTemplateDto) {
        return tardigradeEngine.buildHtml("Popup", dto);
    }

    buildElement(dto: PopupTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: PopupTemplateDto): PopupTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): PopupTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            setUserData(data:any):any {
                let old = (rootElement as any)._tardigradeUserData || null;
                (rootElement as any)._tardigradeUserData = data;
                return old;
            },

            getUserData():any {
                return (rootElement as any)._tardigradeUserData || null;
            },

            content(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Popup", { "content": 0 });
},
contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Popup", hitTest);
                        return (location != null && ("content" in location));
                        }
        };

        return domlet;
    }
}

export var popupTemplate = new PopupTemplate();