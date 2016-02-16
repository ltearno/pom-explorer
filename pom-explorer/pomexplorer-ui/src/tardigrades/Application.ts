"use strict";

import * as tardigrade from "../../node_modules/tardigrade/target/engine/engine";




/**
 * Template's DTO interface.
 * Used to create new template instances */
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
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: ApplicationDto): string {
        Application.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("Application", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:ApplicationDto): HTMLElement {
        return tardigrade.createElement(Application.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:ApplicationDto): Application {
        let element = Application.element(dto);
        return new Application(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): Application {
        return new Application(element);
    }

    /** This constructor should not be called by your application ! */
    constructor(private rootElement: HTMLElement) {}

    /** Returns the root element of this template */
    rootHtmlElement(): HTMLElement { return this.rootElement; }

    /** Sets the user data associated with the root element of the template
     * @return The previous data that was associated, or undefined
     */
    setUserData(data:any): any {
        let old = (this.rootElement as any)._tardigradeUserData || undefined;
        (this.rootElement as any)._tardigradeUserData = data;
        return old;
    }

    /** Returns the user data associated with the root element of the template */
    getUserData():any {
        return (this.rootElement as any)._tardigradeUserData || undefined;
    }

    /** Returns the html element corresponding to the 'drawer' point */
drawer(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "Application", { "drawer": 0 });
}
/** Returns true if the part named 'drawer' with id 'drawer' was hit */
                drawerHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "Application", hitTest);
                        return (location != null && ("drawer" in location));
                        }
/** Returns the html element corresponding to the 'menu' point */
menu(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "Application", { "menu": 0 });
}
/** Returns true if the part named 'menu' with id 'menu' was hit */
                menuHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "Application", hitTest);
                        return (location != null && ("menu" in location));
                        }
/** Returns the html element corresponding to the 'menuItems' point */
menuItems(menuItemsIndex: number): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "Application", { "menuItems": menuItemsIndex });
}
/** Returns the index of the hit part named 'menuItems' with id 'menuItems', -1 if none */
                menuItemsIndex(hitTest:HTMLElement): number {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "Application", hitTest);
                        if (location != null && ("menuItems" in location))
                            return location["menuItems"];
                        return -1;
                        }
/** Builds an HTML string for the 'menuItems' with id 'menuItems' */
buildMenuItems(dto: any): string {
return tardigrade.tardigradeEngine.buildNodeHtml("Application", "menuItems", dto);
}
/** Adds an instance of the 'menuItems' with id 'menuItems' in the collection */
addMenuItems(dto: any): HTMLElement {
let newItem = this.buildMenuItems(dto);
let newElement = tardigrade.createElement(newItem);
this.menu().appendChild(newElement);
return newElement;
}
/** Returns the number of 'menuItems' with id 'menuItems' instances */
countMenuItems(): number {
return this.menu().children.length;
}
/** Returns the html element corresponding to the 'content' point */
content(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "Application", { "content": 0 });
}
/** Returns true if the part named 'content' with id 'content' was hit */
                contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "Application", hitTest);
                        return (location != null && ("content" in location));
                        }

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(Application.loaded)
            return;
        Application.loaded = true;

        

        tardigrade.tardigradeEngine.addTemplate("Application", {e:[null, 0, [""], "div", {"class": "mdl-layout mdl-js-layout mdl-layout--fixed-header"}, [{e:[null, 0, [""], "header", {"class": "mdl-layout__header"}, [{e:[null, 0, [""], "div", {"class": "mdl-layout__header-row"}, [{e:[null, 0, [""], "span", {"class": "mdl-layout-title"}, ["Pom Explorer"]]}, "&nbsp;&nbsp;&nbsp;&nbsp;", {e:[null, 0, [""], "span", {"class": "mdl-badge", "data-badge": "!"}, ["beta"]]}]]}]]}, {e:["drawer", 0, [""], "div", {"class": "mdl-layout__drawer"}, [{e:[null, 0, [""], "span", {"class": "mdl-layout-title"}, ["Pom Explorer"]]}, {e:["menu", 0, [""], "nav", {"class": "mdl-navigation"}, [{e:["menuItems", 1, [""], "a", {"class": "mdl-navigation__link", "href": "#"}, []]}]]}]]}, {e:["content", 0, [""], "main", {"class": "mdl-layout__content content-repositionning"}, []]}]]});
    }
}