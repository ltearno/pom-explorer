"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { SearchPanel } from "./SearchPanel";
import { Card } from "./Card";

export interface ProjectPanelDto {
    _root?: string;
    searchInput?: any;
"@searchInput"?: any;
projectList?: any;
"@projectList"?: any;
cards?: any;
"@cards"?: any;

}

export class ProjectPanel {
    private static loaded = false;

    static ensureLoaded() {
        if(ProjectPanel.loaded)
            return;
        ProjectPanel.loaded = true;

        SearchPanel.ensureLoaded();
Card.ensureLoaded();

        tardigradeEngine.addTemplate("ProjectPanel", new ElementNode(null, <Cardinal>0, [""], "div", {}, [new TemplateNode(null, <Cardinal>0, [""], "SearchPanel", {}, {"input": new PointInfo("searchInput", {}, [])}), new ElementNode("projectList", <Cardinal>0, [""], "div", {"class": "projects-list"}, [new TemplateNode("cards", <Cardinal>1, [""], "Card", {}, {})])]));
    }

    static html(dto: ProjectPanelDto): string {
        ProjectPanel.ensureLoaded();

        return tardigradeEngine.buildHtml("ProjectPanel", dto);
    }

    static element(dto:ProjectPanelDto): HTMLElement {
        return createElement(ProjectPanel.html(dto));
    }

    static create(dto:ProjectPanelDto): ProjectPanel {
        let element = ProjectPanel.element(dto);
        return new ProjectPanel(element);
    }

    static of(element: HTMLElement): ProjectPanel {
        return new ProjectPanel(element);
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

    searchInput(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "ProjectPanel", { "searchInput": 0 });
}
searchInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ProjectPanel", hitTest);
                        return (location != null && ("searchInput" in location));
                        }
projectList(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "ProjectPanel", { "projectList": 0 });
}
projectListHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ProjectPanel", hitTest);
                        return (location != null && ("projectList" in location));
                        }
cards(cardsIndex: number): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "ProjectPanel", { "cards": cardsIndex });
}
cardsDomlet(cardsIndex: number): Card {
let element = this.cards(cardsIndex);
return Card.of(element);
}
cardsHitDomlet(hitElement: HTMLElement): Card {
let location = tardigradeEngine.getLocation(this.rootElement, "ProjectPanel", hitElement);
if(location==null) return null;
if(!("cards" in location)) return null;
return this.cardsDomlet(location["cards"]);
}
cardsIndex(hitTest:HTMLElement): number {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ProjectPanel", hitTest);
                        if (location != null && ("cards" in location))
                            return location["cards"];
                        return -1;
                        }
buildCards(dto: any): string {
return tardigradeEngine.buildNodeHtml("ProjectPanel", "cards", dto);
}
addCards(dto: any): HTMLElement {
let newItem = this.buildCards(dto);
let newElement = createElement(newItem);
this.projectList().appendChild(newElement);
return newElement;
}
countCards(): number {
return this.projectList().children.length;
}
}