"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface ApplicationTemplateDto {
    _root?: string;
    drawer?: any;
"@drawer"?: any;
menu?: any;
"@menu"?: any;
menuItems?: any;
"@menuItems"?: any;
content?: any;
"@content"?: any;

}

export interface ApplicationTemplateElement {
    _root(): HTMLElement;
    // returns the previous data
    setUserData(data:any):any;
    getUserData():any;
    drawer(): HTMLDivElement;
drawerHit(hitTest:HTMLElement): boolean;
menu(): HTMLElement;
menuHit(hitTest:HTMLElement): boolean;
menuItems(menuItemsIndex: number): HTMLElement;
menuItemsIndex(hitTest:HTMLElement): number;
buildMenuItems(dto: any): string;
addMenuItems(dto: any): HTMLElement;
countMenuItems(): number;
content(): HTMLElement;
contentHit(hitTest:HTMLElement): boolean;
}

class ApplicationTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("Application", new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-layout mdl-js-layout mdl-layout--fixed-header"}, [new ElementNode(null, <Cardinal>0, [""], "header", {"class": "mdl-layout__header"}, [new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-layout__header-row"}, [new ElementNode(null, <Cardinal>0, [""], "span", {"class": "mdl-layout-title"}, [new TextNode("Pom Explorer")]), new TextNode("&nbsp;&nbsp;&nbsp;&nbsp;"), new ElementNode(null, <Cardinal>0, [""], "span", {"class": "mdl-badge", "data-badge": "!"}, [new TextNode("beta")])])]), new ElementNode("drawer", <Cardinal>0, [""], "div", {"class": "mdl-layout__drawer"}, [new ElementNode(null, <Cardinal>0, [""], "span", {"class": "mdl-layout-title"}, [new TextNode("Pom Explorer")]), new ElementNode("menu", <Cardinal>0, [""], "nav", {"class": "mdl-navigation"}, [new ElementNode("menuItems", <Cardinal>1, [""], "a", {"class": "mdl-navigation__link", "href": "#"}, [])])]), new ElementNode("content", <Cardinal>0, [""], "main", {"class": "mdl-layout__content content-repositionning"}, [])]));
    }

    buildHtml(dto: ApplicationTemplateDto) {
        return tardigradeEngine.buildHtml("Application", dto);
    }

    buildElement(dto: ApplicationTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: ApplicationTemplateDto): ApplicationTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): ApplicationTemplateElement {
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

            drawer(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Application", { "drawer": 0 });
},
drawerHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Application", hitTest);
                        return (location != null && ("drawer" in location));
                        },
menu(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Application", { "menu": 0 });
},
menuHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Application", hitTest);
                        return (location != null && ("menu" in location));
                        },
menuItems(menuItemsIndex: number): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Application", { "menuItems": menuItemsIndex });
},
menuItemsIndex(hitTest:HTMLElement): number {
                        let location = tardigradeEngine.getLocation(rootElement, "Application", hitTest);
                        if (location != null && ("menuItems" in location))
                            return location["menuItems"];
                        return -1;
                        },
buildMenuItems(dto: any): string {
return tardigradeEngine.buildNodeHtml("Application", "menuItems", dto);
},
addMenuItems(dto: any): HTMLElement {
let newItem = domlet.buildMenuItems(dto);
let newElement = createElement(newItem);
domlet.menu().appendChild(newElement);
return newElement;
},
countMenuItems(): number {
return domlet.menu().children.length;
},
content(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Application", { "content": 0 });
},
contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Application", hitTest);
                        return (location != null && ("content" in location));
                        }
        };

        return domlet;
    }
}

export var applicationTemplate = new ApplicationTemplate();