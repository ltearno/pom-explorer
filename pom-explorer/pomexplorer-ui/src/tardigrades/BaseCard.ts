"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface BaseCardTemplateDto {
    _root?: string;
    title?: any;
"@title"?: any;
content?: any;
"@content"?: any;
details?: any;
"@details"?: any;
actions?: any;
"@actions"?: any;

}

export interface BaseCardTemplateElement {
    _root(): HTMLElement;
    title(): HTMLDivElement;
content(): HTMLDivElement;
details(): HTMLDivElement;
actions(): HTMLDivElement;
}

class BaseCardTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("BaseCard", new ElementNode(null, <Cardinal>0, [""], "div", {"class": "project-card mdl-card mdl-shadow--2dp"}, [new ElementNode("title", <Cardinal>0, [""], "div", {"class": "mdl-card__title mdl-card--expand"}, []), new ElementNode("content", <Cardinal>0, [""], "div", {"class": "mdl-card__supporting-text"}, []), new ElementNode("details", <Cardinal>0, [""], "div", {"class": "mdl-card__supporting-text", "style": "display:none;"}, []), new ElementNode("actions", <Cardinal>0, [""], "div", {"class": "mdl-card__actions mdl-card--border"}, [])]));
    }

    buildHtml(dto: BaseCardTemplateDto) {
        return tardigradeEngine.buildHtml("BaseCard", dto);
    }

    buildElement(dto: BaseCardTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: BaseCardTemplateDto): BaseCardTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): BaseCardTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            title(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "title": 0 });
},
content(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "content": 0 });
},
details(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "details": 0 });
},
actions(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "actions": 0 });
}
        };

        return domlet;
    }
}

export var baseCardTemplate = new BaseCardTemplate();