"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
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
    drawer(): HTMLDivElement;
menu(): HTMLElement;
menuItems(menuItemsIndex: number): HTMLElement;
menuItemsIndex(hitTest:HTMLElement): number;
buildMenuItems(dto: any): string;
addMenuItems(dto: any): HTMLElement;
countMenuItems(): number;
content(): HTMLElement;
}

class ApplicationTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("Application", tardigradeParser.parseTemplate(`<html>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div x-id="drawer" class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav x-id="menu" class="mdl-navigation">
            <a x-id="menuItems" x-cardinal="*" class="mdl-navigation__link" href="#"/>
        </nav>
    </div>
    <main x-id="content" class="mdl-layout__content content-repositionning"/>
</div>
</body>
</html>`));
    }

    buildHtml(dto: ApplicationTemplateDto) {
        return tardigradeEngine.buildHtml("Application", dto);
    }

    buildElement(dto: ApplicationTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): ApplicationTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            drawer(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Application", { "drawer": 0 });
},
menu(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Application", { "menu": 0 });
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
}
        };

        return domlet;
    }
}

export var applicationTemplate = new ApplicationTemplate();