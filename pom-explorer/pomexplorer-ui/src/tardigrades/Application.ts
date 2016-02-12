"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface ApplicationDto {
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

export class Application {
    private static loaded = false;

    static ensureLoaded() {
        if(Application.loaded)
            return;
        Application.loaded = true;

        

        tardigradeEngine.addTemplate("Application", new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-layout mdl-js-layout mdl-layout--fixed-header"}, [new ElementNode(null, <Cardinal>0, [""], "header", {"class": "mdl-layout__header"}, [new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-layout__header-row"}, [new ElementNode(null, <Cardinal>0, [""], "span", {"class": "mdl-layout-title"}, [new TextNode("Pom Explorer")]), new TextNode("&nbsp;&nbsp;&nbsp;&nbsp;"), new ElementNode(null, <Cardinal>0, [""], "span", {"class": "mdl-badge", "data-badge": "!"}, [new TextNode("beta")])])]), new ElementNode("drawer", <Cardinal>0, [""], "div", {"class": "mdl-layout__drawer"}, [new ElementNode(null, <Cardinal>0, [""], "span", {"class": "mdl-layout-title"}, [new TextNode("Pom Explorer")]), new ElementNode("menu", <Cardinal>0, [""], "nav", {"class": "mdl-navigation"}, [new ElementNode("menuItems", <Cardinal>1, [""], "a", {"class": "mdl-navigation__link", "href": "#"}, [])])]), new ElementNode("content", <Cardinal>0, [""], "main", {"class": "mdl-layout__content content-repositionning"}, [])]));
    }

    static html(dto: ApplicationDto): string {
        Application.ensureLoaded();

        return tardigradeEngine.buildHtml("Application", dto);
    }

    static element(dto:ApplicationDto): HTMLElement {
        return createElement(Application.html(dto));
    }

    static create(dto:ApplicationDto): Application {
        let element = Application.element(dto);
        return new Application(element);
    }

    static of(element: HTMLElement): Application {
        return new Application(element);
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

    drawer(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Application", { "drawer": 0 });
}
drawerHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Application", hitTest);
                        return (location != null && ("drawer" in location));
                        }
menu(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Application", { "menu": 0 });
}
menuHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Application", hitTest);
                        return (location != null && ("menu" in location));
                        }
menuItems(menuItemsIndex: number): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Application", { "menuItems": menuItemsIndex });
}
menuItemsIndex(hitTest:HTMLElement): number {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Application", hitTest);
                        if (location != null && ("menuItems" in location))
                            return location["menuItems"];
                        return -1;
                        }
buildMenuItems(dto: any): string {
return tardigradeEngine.buildNodeHtml("Application", "menuItems", dto);
}
addMenuItems(dto: any): HTMLElement {
let newItem = this.buildMenuItems(dto);
let newElement = createElement(newItem);
this.menu().appendChild(newElement);
return newElement;
}
countMenuItems(): number {
return this.menu().children.length;
}
content(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Application", { "content": 0 });
}
contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Application", hitTest);
                        return (location != null && ("content" in location));
                        }
}