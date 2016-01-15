"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

export interface MenuItemsDto {
    _root?: string;
}

export interface ApplicationTemplateDto {
    _root?: string;
    Drawer?: string;
    Menu?: string;
    MenuItems?: string | string[] | MenuItemsDto | MenuItemsDto[] | any;
    Content?: string;
}

export interface ApplicationTemplateElement {
    _root(): HTMLElement;
    content(): HTMLElement;
    menu(): HTMLElement;
    menuItems(menuItemsIndex: number): HTMLElement;
    drawer(): HTMLElement;
    menuItemsIndex(hitTest: HTMLElement): number;
    addMenuItem(dto: MenuItemsDto);
}

class ApplicationTemplate {
    ensureLoaded() {
    }
    
    constructor() {
        tardigradeEngine.addTemplate("Application", tardigradeParser.parseTemplate(`
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div x-id="Drawer" class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav x-id="Menu" class="mdl-navigation">
            <a x-id="MenuItems" x-cardinal="*" class="mdl-navigation__link" href="#"/>
        </nav>
    </div>
    <main x-id="Content" class="mdl-layout__content content-repositionning"/>
</div>
`));
    }

    buildHtml(dto: ApplicationTemplateDto) {
        return tardigradeEngine.buildHtml("Application", dto);
    }

    buildElement(dto: ApplicationTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): ApplicationTemplateElement {
        let me = {
            _root() { return rootElement; },

            content(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Application", { "Content": 0 });
            },

            menu() {
                return tardigradeEngine.getPoint(rootElement, "Application", { "Menu": 0 });
            },

            menuItems(menuItemsIndex: number) {
                return tardigradeEngine.getPoint(rootElement, "Application", { "Menu": 0, "MenuItems": menuItemsIndex });
            },

            drawer() {
                return tardigradeEngine.getPoint(rootElement, "Application", { "Drawer": 0 });
            },

            menuItemsIndex(hitTest: HTMLElement) {
                let location = tardigradeEngine.getLocation(rootElement, "Application", hitTest);
                if (location != null && ("MenuItems" in location))
                    return location["MenuItems"];
                return -1;
            },

            // TODO not sure
            addMenuItem(dto: MenuItemsDto) {
                let menuItem = tardigradeEngine.buildNodeHtml("Application", "MenuItems", dto);
                me.menu().appendChild(createElement(menuItem));
            }
        };

        return me;
    }
}

export var applicationTemplate = new ApplicationTemplate();