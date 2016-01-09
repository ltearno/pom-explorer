import { MaterialDomlet } from "./MaterialDomlet";
import { buildHtmlElement } from "./Utils";

import { TardigradeEngine } from "./node_modules/tardigrade/target/engine/engine";
import { createElement, domChain, indexOf } from "./node_modules/tardigrade/target/engine/runtime";

TardigradeEngine.addTemplate("MenuItem", `<a x-id="Title" class="mdl-navigation__link" href="#"></a>`);

TardigradeEngine.addTemplate("Application", `
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div x-id="Drawer" class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav x-id="Menu" class="mdl-navigation">
            <MenuItem x-id="MenuItems" x-cardinal="*"/>
        </nav>
    </div>
    <main x-id="Content" class="mdl-layout__content content-repositionning">
    </main>
</div>
`);

declare var componentHandler: any;

function initMaterialElement(e: HTMLElement) {
    if (e == null)
        return;

    var upgrade = false;
    for (var i = 0; i < e.classList.length; i++)
        if (e.classList[i].indexOf("mdl-") >= 0) {
            upgrade = true;
            break;
        }

    if (upgrade)
        componentHandler.upgradeElement(e);

    for (var c in e.children) {
        if (e.children[c] instanceof HTMLElement)
            initMaterialElement(<HTMLElement>e.children[c]);
    }
}

export class ApplicationPanel {
    element: HTMLElement;

    constructor() {
        this.element = createElement(TardigradeEngine.buildHtml("Application", {}));
        initMaterialElement(this.element);
    }

    addMenuHandler(handler: { (index: number, menuItem: HTMLElement, event: any): void; }) {
        var menu = TardigradeEngine.getPoint(this.element, "Application", { "Menu": 0 });
        menu.addEventListener("click", (e) => {
            var target = <HTMLElement>e.target;

            var location = TardigradeEngine.getLocation(this.element, "Application", target);
            if (location != null && ("MenuItems" in location)) {
                let index = location["MenuItems"];
                let menuItem = TardigradeEngine.getPoint(this.element, "Application", location);
                handler(index, menuItem, e);
                this.hideDrawer();
            }
        });
    }

    addMenuItem(name: string) {
        var menu = TardigradeEngine.getPoint(this.element, "Application", { "Menu": 0 });
        menu.appendChild(createElement(TardigradeEngine.buildHtml("MenuItem", { "Title": name })));
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>this.element;
    }

    content(): HTMLDivElement {
        return <HTMLDivElement>TardigradeEngine.getPoint(this.element, "Application", { "Content": 0 });
    }

    protected hideDrawer() {
        // fix : the obfuscator is still visible if only remove is-visible from the drawer
        document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
        TardigradeEngine.getPoint(this.element, "Application", { "Drawer": 0 }).classList.remove("is-visible");
    }
}