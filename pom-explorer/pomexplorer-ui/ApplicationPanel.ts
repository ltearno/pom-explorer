import { MaterialDomlet } from "./MaterialDomlet";
import { initMaterialElement } from "./Utils";

import { tardigradeEngine } from "./node_modules/tardigrade/target/engine/engine";
import { createElement, domChain, indexOf } from "./node_modules/tardigrade/target/engine/runtime";

interface MenuItemsDto {
    _root?: string;
}

interface ApplicationTemplateDto {
    _root?: string;
    Drawer?: string;
    Menu?: string;
    MenuItems?: string | string[] | MenuItemsDto | MenuItemsDto[];
    Content?: string;
}

class ApplicationTemplate {
    private id: string;

    constructor() {
        this.id = "Application";

        tardigradeEngine.addTemplate(this.id, `
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
`);
    }

    buildHtml(dto: ApplicationTemplateDto) {
        return tardigradeEngine.buildHtml(this.id, dto);
    }

    buildElement(dto: ApplicationTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    content(rootElement: HTMLElement): HTMLDivElement {
        return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, this.id, { "Content": 0 });
    }

    menu(rootElement: HTMLElement) {
        return tardigradeEngine.getPoint(rootElement, this.id, { "Menu": 0 });
    }

    menuItems(rootElement: HTMLElement, menuItemsIndex: number) {
        return tardigradeEngine.getPoint(rootElement, this.id, { "Menu": 0, "MenuItems": menuItemsIndex });
    }

    drawer(rootElement: HTMLElement) {
        return tardigradeEngine.getPoint(rootElement, this.id, { "Drawer": 0 });
    }

    menuItemsIndex(rootElement: HTMLElement, hitTest: HTMLElement) {
        let location = this.getLocation(rootElement, hitTest);
        if (location != null && ("MenuItems" in location))
            return location["MenuItems"];
        return -1;
    }

    addMenuItem(rootElement: HTMLElement, dto: MenuItemsDto) {
        // grab the parent node
        // TODO this may be not doable if the parent does not have an id...
        // TODO think about a better solution...
        let menu = applicationTemplate.menu(rootElement);
        let menuItem = this.buildNodeHtml("MenuItems", dto);
        menu.appendChild(createElement(menuItem));
    }

    private getLocation(rootElement: HTMLElement, hitTest: HTMLElement) {
        return tardigradeEngine.getLocation(rootElement, this.id, hitTest);
    }

    private buildNodeHtml(nodeId: string, dto: {}) {
        return tardigradeEngine.buildNodeHtml(this.id, nodeId, dto);
    }
}

var applicationTemplate = new ApplicationTemplate();

export class ApplicationPanel {
    element: HTMLElement;

    constructor() {
        this.element = applicationTemplate.buildElement({});
        initMaterialElement(this.element);
        applicationTemplate.menu(this.element).innerHTML = "";
    }

    addMenuHandler(handler: { (index: number, menuName: string, event: any): void; }) {
        var menu = applicationTemplate.menu(this.element);
        menu.addEventListener("click", (e) => {
            var target = <HTMLElement>e.target;

            let menuItemsIndex = applicationTemplate.menuItemsIndex(this.element, target);
            if (menuItemsIndex >= 0) {
                let menuItem = applicationTemplate.menuItems(this.element, menuItemsIndex);
                handler(menuItemsIndex, menuItem.innerText, e);
                this.hideDrawer();
            }
        });
    }

    addMenuItem(name: string) {
        applicationTemplate.addMenuItem(this.element, { _root: name});
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>this.element;
    }

    setContent(contentElement: HTMLElement) {
        let content = applicationTemplate.content(this.element);
        content.innerHTML = "";
        if (contentElement != null)
            content.appendChild(contentElement);
    }

    protected hideDrawer() {
        // fix : the obfuscator is still visible if only remove is-visible from the drawer
        document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
        applicationTemplate.drawer(this.element).classList.remove("is-visible");
    }
}
