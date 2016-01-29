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
    content(): HTMLDivElement;
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

            content(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Popup", { "content": 0 });
}
        };

        return domlet;
    }
}

export var popupTemplate = new PopupTemplate();