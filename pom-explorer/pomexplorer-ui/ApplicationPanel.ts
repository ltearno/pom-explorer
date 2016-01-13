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

interface ApplicationTemplateElement {
    _root(): HTMLElement;
    content(): HTMLElement;
    menu(): HTMLElement;
    menuItems(menuItemsIndex: number): HTMLElement;
    drawer(): HTMLElement;
    menuItemsIndex(hitTest: HTMLElement): number;
    addMenuItem(dto: MenuItemsDto);
}

class ApplicationTemplate {
    constructor() {
        tardigradeEngine.addTemplate("Application", `
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

var applicationTemplate = new ApplicationTemplate();

export class ApplicationPanel {
    template: ApplicationTemplateElement;

    constructor() {
        this.template = applicationTemplate.of(applicationTemplate.buildElement({}));
        initMaterialElement(this.template._root());
        this.template.menu().innerHTML = "";
    }

    addMenuHandler(handler: { (index: number, menuName: string, event: any): void; }) {
        var menu = this.template.menu();
        menu.addEventListener("click", (e) => {
            var target = <HTMLElement>e.target;

            let menuItemsIndex = this.template.menuItemsIndex(target);
            if (menuItemsIndex >= 0) {
                let menuItem = this.template.menuItems(menuItemsIndex);
                handler(menuItemsIndex, menuItem.innerText, e);
                this.hideDrawer();
            }
        });
    }

    addMenuItem(name: string) {
        this.template.addMenuItem({ _root: name });
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>this.template._root();
    }

    setContent(contentElement: HTMLElement) {
        let content = this.template.content();
        content.innerHTML = "";
        if (contentElement != null)
            content.appendChild(contentElement);
    }

    protected hideDrawer() {
        // fix : the obfuscator is still visible if only remove is-visible from the drawer
        document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
        this.template.drawer().classList.remove("is-visible");
    }
}
