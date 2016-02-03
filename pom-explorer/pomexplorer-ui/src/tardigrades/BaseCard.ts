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
menu?: any;
"@menu"?: any;

}

export interface BaseCardTemplateElement {
    _root(): HTMLElement;
    // returns the previous data
    setUserData(data:any):any;
    getUserData():any;
    title(): HTMLDivElement;
titleHit(hitTest:HTMLElement): boolean;
content(): HTMLDivElement;
contentHit(hitTest:HTMLElement): boolean;
details(): HTMLDivElement;
detailsHit(hitTest:HTMLElement): boolean;
actions(): HTMLDivElement;
actionsHit(hitTest:HTMLElement): boolean;
menu(): HTMLDivElement;
menuHit(hitTest:HTMLElement): boolean;
}

class BaseCardTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("BaseCard", new ElementNode(null, <Cardinal>0, [""], "div", {"class": "project-card mdl-card mdl-shadow--2dp"}, [new ElementNode("title", <Cardinal>0, [""], "div", {"class": "mdl-card__title mdl-card--expand"}, []), new ElementNode("content", <Cardinal>0, [""], "div", {"class": "mdl-card__supporting-text"}, []), new ElementNode("details", <Cardinal>0, [""], "div", {"class": "mdl-card__supporting-text", "style": "display:none;"}, []), new ElementNode("actions", <Cardinal>0, [""], "div", {"class": "mdl-card__actions mdl-card--border"}, []), new ElementNode("menu", <Cardinal>0, [""], "div", {"class": "mdl-card__menu"}, [])]));
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

            setUserData(data:any):any {
                let old = (rootElement as any)._tardigradeUserData || null;
                (rootElement as any)._tardigradeUserData = data;
                return old;
            },

            getUserData():any {
                return (rootElement as any)._tardigradeUserData || null;
            },

            title(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "title": 0 });
},
titleHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "BaseCard", hitTest);
                        return (location != null && ("title" in location));
                        },
content(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "content": 0 });
},
contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "BaseCard", hitTest);
                        return (location != null && ("content" in location));
                        },
details(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "details": 0 });
},
detailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "BaseCard", hitTest);
                        return (location != null && ("details" in location));
                        },
actions(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "actions": 0 });
},
actionsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "BaseCard", hitTest);
                        return (location != null && ("actions" in location));
                        },
menu(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "BaseCard", { "menu": 0 });
},
menuHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "BaseCard", hitTest);
                        return (location != null && ("menu" in location));
                        }
        };

        return domlet;
    }
}

export var baseCardTemplate = new BaseCardTemplate();