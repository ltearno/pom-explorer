"use strict";

import { initMaterialElement } from "./Utils";
import { Application as ApplicationTemplate } from "./tardigrades/Application";

export class ApplicationPanel {
    template: ApplicationTemplate;

    constructor() {
        this.template = ApplicationTemplate.create({});
        initMaterialElement(this.template.rootHtmlElement());
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
        this.template.addMenuItems({ _root: name });
    }

    main(): HTMLDivElement {
        return <HTMLDivElement>this.template.rootHtmlElement();
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
