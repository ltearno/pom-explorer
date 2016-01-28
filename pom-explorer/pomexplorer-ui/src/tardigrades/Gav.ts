"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface GavTemplateDto {
    _root?: string;
    groupId?: any;
"@groupId"?: any;
artifactId?: any;
"@artifactId"?: any;
version?: any;
"@version"?: any;

}

export interface GavTemplateElement {
    _root(): HTMLElement;
    groupId(): HTMLDivElement;
artifactId(): HTMLDivElement;
version(): HTMLDivElement;
}

class GavTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("Gav", new ElementNode(null, <Cardinal>0, [""], "h2", {"class": "mdl-card__title-text"}, [new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode("groupId", <Cardinal>0, [""], "div", {}, []), new ElementNode("artifactId", <Cardinal>0, [""], "div", {}, []), new ElementNode("version", <Cardinal>0, [""], "div", {}, [])])]));
    }

    buildHtml(dto: GavTemplateDto) {
        return tardigradeEngine.buildHtml("Gav", dto);
    }

    buildElement(dto: GavTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: GavTemplateDto): GavTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): GavTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            groupId(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Gav", { "groupId": 0 });
},
artifactId(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Gav", { "artifactId": 0 });
},
version(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Gav", { "version": 0 });
}
        };

        return domlet;
    }
}

export var gavTemplate = new GavTemplate();