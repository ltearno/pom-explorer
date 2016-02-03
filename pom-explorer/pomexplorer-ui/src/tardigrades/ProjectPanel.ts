"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { searchPanelTemplate, SearchPanelTemplateElement } from "./SearchPanel";
import { cardTemplate, CardTemplateElement } from "./Card";

export interface ProjectPanelTemplateDto {
    _root?: string;
    searchInput?: any;
"@searchInput"?: any;
projectList?: any;
"@projectList"?: any;
cards?: any;
"@cards"?: any;

}

export interface ProjectPanelTemplateElement {
    _root(): HTMLElement;
    // returns the previous data
    setUserData(data:any):any;
    getUserData():any;
    searchInput(): HTMLElement;
searchInputHit(hitTest:HTMLElement): boolean;
projectList(): HTMLDivElement;
projectListHit(hitTest:HTMLElement): boolean;
cards(cardsIndex: number): HTMLElement;
cardsDomlet(cardsIndex: number): CardTemplateElement;
cardsIndex(hitTest:HTMLElement): number;
buildCards(dto: any): string;
addCards(dto: any): HTMLElement;
countCards(): number;
}

class ProjectPanelTemplate {
    ensureLoaded() {
    }

    constructor() {
        searchPanelTemplate.ensureLoaded();
cardTemplate.ensureLoaded();

        tardigradeEngine.addTemplate("ProjectPanel", new ElementNode(null, <Cardinal>0, [""], "div", {}, [new TemplateNode(null, <Cardinal>0, [""], "SearchPanel", {}, {"input": new PointInfo("searchInput", {}, [])}), new ElementNode("projectList", <Cardinal>0, [""], "div", {"class": "projects-list"}, [new TemplateNode("cards", <Cardinal>1, [""], "Card", {}, {})])]));
    }

    buildHtml(dto: ProjectPanelTemplateDto) {
        return tardigradeEngine.buildHtml("ProjectPanel", dto);
    }

    buildElement(dto: ProjectPanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: ProjectPanelTemplateDto): ProjectPanelTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): ProjectPanelTemplateElement {
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

            searchInput(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "searchInput": 0 });
},
searchInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "ProjectPanel", hitTest);
                        return (location != null && ("searchInput" in location));
                        },
projectList(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "projectList": 0 });
},
projectListHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "ProjectPanel", hitTest);
                        return (location != null && ("projectList" in location));
                        },
cards(cardsIndex: number): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "cards": cardsIndex });
},
cardsDomlet(cardsIndex: number): CardTemplateElement {
let element = domlet.cards(cardsIndex);
return cardTemplate.of(element);
},
cardsIndex(hitTest:HTMLElement): number {
                        let location = tardigradeEngine.getLocation(rootElement, "ProjectPanel", hitTest);
                        if (location != null && ("cards" in location))
                            return location["cards"];
                        return -1;
                        },
buildCards(dto: any): string {
return tardigradeEngine.buildNodeHtml("ProjectPanel", "cards", dto);
},
addCards(dto: any): HTMLElement {
let newItem = domlet.buildCards(dto);
let newElement = createElement(newItem);
domlet.projectList().appendChild(newElement);
return newElement;
},
countCards(): number {
return domlet.projectList().children.length;
}
        };

        return domlet;
    }
}

export var projectPanelTemplate = new ProjectPanelTemplate();