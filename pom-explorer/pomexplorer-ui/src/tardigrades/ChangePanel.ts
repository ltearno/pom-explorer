"use strict";

import * as tardigrade from "../../node_modules/tardigrade/target/engine/engine";




/**
 * Template's DTO interface.
 * Used to create new template instances */
export interface ChangePanelDto {
    _root?: string;
    graphChanges?: any;
"@graphChanges"?: any;
projectChanges?: any;
"@projectChanges"?: any;

}

export class ChangePanel {
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: ChangePanelDto): string {
        ChangePanel.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("ChangePanel", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:ChangePanelDto): HTMLElement {
        return tardigrade.createElement(ChangePanel.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:ChangePanelDto): ChangePanel {
        let element = ChangePanel.element(dto);
        return new ChangePanel(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): ChangePanel {
        return new ChangePanel(element);
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

    /** Returns the html element corresponding to the 'graphChanges' point */
graphChanges(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangePanel", { "graphChanges": 0 });
}
/** Returns true if the part named 'graphChanges' with id 'graphChanges' was hit */
                graphChangesHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangePanel", hitTest);
                        return (location != null && ("graphChanges" in location));
                        }
/** Returns the html element corresponding to the 'projectChanges' point */
projectChanges(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangePanel", { "projectChanges": 0 });
}
/** Returns true if the part named 'projectChanges' with id 'projectChanges' was hit */
                projectChangesHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangePanel", hitTest);
                        return (location != null && ("projectChanges" in location));
                        }

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(ChangePanel.loaded)
            return;
        ChangePanel.loaded = true;

        

        tardigrade.tardigradeEngine.addTemplate("ChangePanel", {e:[null, 0, [""], "div", {}, [{e:[null, 0, [""], "div", {}, [{e:[null, 0, [""], "h2", {}, ["Graph changes"]]}, {e:["graphChanges", 0, [""], "div", {}, []]}]]}, {e:[null, 0, [""], "div", {}, [{e:[null, 0, [""], "h2", {}, ["Project changes"]]}, {e:["projectChanges", 0, [""], "div", {}, []]}]]}]]});
    }
}