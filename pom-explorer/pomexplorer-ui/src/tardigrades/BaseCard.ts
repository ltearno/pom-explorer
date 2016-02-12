"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface BaseCardDto {
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

export class BaseCard {
    private static loaded = false;

    static ensureLoaded() {
        if(BaseCard.loaded)
            return;
        BaseCard.loaded = true;

        

        tardigradeEngine.addTemplate("BaseCard", new ElementNode(null, <Cardinal>0, [""], "div", {"class": "project-card mdl-card mdl-shadow--2dp"}, [new ElementNode("title", <Cardinal>0, [""], "div", {"class": "mdl-card__title mdl-card--expand"}, []), new ElementNode("content", <Cardinal>0, [""], "div", {"class": "mdl-card__supporting-text"}, []), new ElementNode("details", <Cardinal>0, [""], "div", {"class": "mdl-card__supporting-text", "style": "display:none;"}, []), new ElementNode("actions", <Cardinal>0, [""], "div", {"class": "mdl-card__actions mdl-card--border"}, []), new ElementNode("menu", <Cardinal>0, [""], "div", {"class": "mdl-card__menu"}, [])]));
    }

    static html(dto: BaseCardDto): string {
        BaseCard.ensureLoaded();

        return tardigradeEngine.buildHtml("BaseCard", dto);
    }

    static element(dto:BaseCardDto): HTMLElement {
        return createElement(BaseCard.html(dto));
    }

    static create(dto:BaseCardDto): BaseCard {
        let element = BaseCard.element(dto);
        return new BaseCard(element);
    }

    static of(element: HTMLElement): BaseCard {
        return new BaseCard(element);
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

    title(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "title": 0 });
}
titleHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("title" in location));
                        }
content(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "content": 0 });
}
contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("content" in location));
                        }
details(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "details": 0 });
}
detailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("details" in location));
                        }
actions(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "actions": 0 });
}
actionsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("actions" in location));
                        }
menu(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "BaseCard", { "menu": 0 });
}
menuHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "BaseCard", hitTest);
                        return (location != null && ("menu" in location));
                        }
}