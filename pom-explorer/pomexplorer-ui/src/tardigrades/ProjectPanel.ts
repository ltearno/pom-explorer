"use strict";

declare var tardigrade;

import { SearchPanel } from "./SearchPanel";
import { Card } from "./Card";

export type BaseDto = string | {
    _?: string; // node's text content
    $?: { [attributeName:string]: string | number }; // node's attribute values
}

/**
 * Template's DTO interface.
 * Used to create new template instances */

export type ProjectListDto = string | {
    _?: string; // node's text content
    $?: { [attributeName:string]: string | number }; // node's attribute values
    cards?: BaseDto[] | BaseDto;
}
export type _RootDto = string | {
    _?: string; // node's text content
    $?: { [attributeName:string]: string | number }; // node's attribute values
    searchInput?: BaseDto;
    cards?: BaseDto[] | BaseDto;
    projectList?: ProjectListDto;
    _root?: BaseDto;
}


export class ProjectPanel {
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: _RootDto): string {
        ProjectPanel.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("ProjectPanel", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto: _RootDto): HTMLElement {
        return tardigrade.createElement(ProjectPanel.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto: _RootDto): ProjectPanel {
        let element = ProjectPanel.element(dto);
        return new ProjectPanel(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: Element): ProjectPanel {
        return new ProjectPanel(element);
    }

    private rootElement: HTMLElement;

    /** This constructor should not be called by your application ! */
    constructor(rootElement: Element) {
        this.rootElement = <HTMLElement>rootElement;
    }

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

    /** Returns the html element corresponding to the 'searchInput' point */
searchInput(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ProjectPanel", { "searchInput": 0 });
}
/** Returns true if the part named 'searchInput' with id 'searchInput' was hit */
                searchInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ProjectPanel", hitTest);
                        return (location != null && ("searchInput" in location));
                        }
/** Returns the html element corresponding to the 'projectList' point */
projectList(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ProjectPanel", { "projectList": 0 });
}
/** Returns true if the part named 'projectList' with id 'projectList' was hit */
                projectListHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ProjectPanel", hitTest);
                        return (location != null && ("projectList" in location));
                        }
/** Returns the html element corresponding to the 'cards' point */
cards(cardsIndex: number): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ProjectPanel", { "cards": cardsIndex });
}
/** Returns the template instance for the point 'cards' with id 'cards' */
cardsDomlet(cardsIndex: number): Card {
let element = this.cards(cardsIndex);
return Card.of(element);
}
/** Returns the 'cards' with id 'cards' template instance that is hit by the hitElement */
cardsHitDomlet(hitElement: HTMLElement): Card {
let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ProjectPanel", hitElement);
if(location==null) return null;
if(!("cards" in location)) return null;
return this.cardsDomlet(location["cards"]);
}
/** Returns the index of the hit part named 'cards' with id 'cards', -1 if none */
                cardsIndex(hitTest:HTMLElement): number {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ProjectPanel", hitTest);
                        if (location != null && ("cards" in location))
                            return location["cards"];
                        return -1;
                        }
/** Builds an HTML string for the 'cards' with id 'cards' */
buildCards(dto: any): string {
return tardigrade.tardigradeEngine.buildNodeHtml("ProjectPanel", "cards", dto);
}
/** Adds an instance of the 'cards' with id 'cards' in the collection */
addCards(dto: any): HTMLElement {
let newItem = this.buildCards(dto);
let newElement = tardigrade.createElement(newItem);
this.projectList().appendChild(newElement);
return newElement;
}
/** Returns the number of 'cards' with id 'cards' instances */
countCards(): number {
return this.projectList().children.length;
}

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(ProjectPanel.loaded)
            return;
        ProjectPanel.loaded = true;

        SearchPanel.ensureLoaded();
Card.ensureLoaded();

        tardigrade.tardigradeEngine.addTemplate("ProjectPanel", {e:[null, 0, [""], "div", {}, [{t:[null, 0, [""], "SearchPanel", {}, {"input": ["searchInput", {}, []]}]}, {e:["projectList", 0, [""], "div", {"class": "projects-list"}, [{t:["cards", 1, [""], "Card", {}, {}]}]]}]]});
    }
}