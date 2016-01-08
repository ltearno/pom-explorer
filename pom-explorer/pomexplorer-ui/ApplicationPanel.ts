import { MaterialDomlet } from "./MaterialDomlet";
import { buildHtmlElement } from "./Utils";

import { TardigradeEngine as e } from "./node_modules/tardigrade/target/engine/engine";
import { createElement, domChain, indexOf } from "./node_modules/tardigrade/target/engine/runtime";

var ApplicationPanelDomlet = new MaterialDomlet(`
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav class="mdl-navigation">
        </nav>
    </div>
    <main class="mdl-layout__content content-repositionning">
    </main>
</div>
`, {
        'main': [],
        'content': [2],
        'menu': [1, 1],
        'drawer': [1]
    });

export class ApplicationPanel {
    element: HTMLElement;

    constructor() {
        this.element = ApplicationPanelDomlet.htmlElement();
    }

    addMenuHandler(handler: { (index: number, menuItem: HTMLElement, event: any): void; }) {
        var menu = ApplicationPanelDomlet.point("menu", this.element);
        menu.addEventListener("click", (e) => {
            var target = <HTMLElement>e.target;
            var comingMenuItem = ApplicationPanelDomlet.getComingChild(menu, target, this.element);
            var index = indexOf(menu, comingMenuItem);

            handler(index, comingMenuItem, e);

            this.hideDrawer();
        });
    }

    addMenuItem(name: string) {
        var menu = ApplicationPanelDomlet.point("menu", this.element);
        menu.appendChild(buildHtmlElement(`<a class="mdl-navigation__link" href="#">${name}</a>`));
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>ApplicationPanelDomlet.point("main", this.element);
    }

    content(): HTMLDivElement {
        return <HTMLDivElement>ApplicationPanelDomlet.point("content", this.element);
    }

    protected hideDrawer() {
        // fix : the obfuscator is still visible if only remove is-visible from the drawer
        document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
        ApplicationPanelDomlet.point("drawer", this.element).classList.remove("is-visible");
    }
}