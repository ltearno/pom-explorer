"use strict";

import * as tardigrade from "../../node_modules/tardigrade/target/engine/engine";




/**
 * Template's DTO interface.
 * Used to create new template instances */
export interface ConsolePanelDto {
    _root?: string;
    output?: any;
"@output"?: any;
input?: any;
"@input"?: any;

}

export class ConsolePanel {
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: ConsolePanelDto): string {
        ConsolePanel.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("ConsolePanel", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:ConsolePanelDto): HTMLElement {
        return tardigrade.createElement(ConsolePanel.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:ConsolePanelDto): ConsolePanel {
        let element = ConsolePanel.element(dto);
        return new ConsolePanel(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): ConsolePanel {
        return new ConsolePanel(element);
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

    /** Returns the html element corresponding to the 'output' point */
output(): HTMLDivElement {
return <HTMLDivElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ConsolePanel", { "output": 0 });
}
/** Returns true if the part named 'output' with id 'output' was hit */
                outputHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ConsolePanel", hitTest);
                        return (location != null && ("output" in location));
                        }
/** Returns the html element corresponding to the 'input' point */
input(): HTMLInputElement {
return <HTMLInputElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ConsolePanel", { "input": 0 });
}
/** Returns true if the part named 'input' with id 'input' was hit */
                inputHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ConsolePanel", hitTest);
                        return (location != null && ("input" in location));
                        }

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(ConsolePanel.loaded)
            return;
        ConsolePanel.loaded = true;

        

        tardigrade.tardigradeEngine.addTemplate("ConsolePanel", {e:[null, 0, [""], "div", {"class": "console-panel"}, [{e:["output", 0, [""], "div", {"class": "console-output"}, []]}, {e:[null, 0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label"}, [{e:["input", 0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "sample3"}, []]}, {e:[null, 0, [""], "label", {"class": "mdl-textfield__label", "for": "sample3"}, ["enter a command, or just \"?\" to get help"]]}]]}]]});
    }
}