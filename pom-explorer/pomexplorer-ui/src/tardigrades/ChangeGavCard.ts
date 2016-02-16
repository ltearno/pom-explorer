"use strict";

import * as tardigrade from "../../node_modules/tardigrade/target/engine/engine";


import { BaseCard } from "./BaseCard";

/**
 * Template's DTO interface.
 * Used to create new template instances */
export interface ChangeGavCardDto {
    _root?: string;
    groupId?: any;
"@groupId"?: any;
artifactId?: any;
"@artifactId"?: any;
version?: any;
"@version"?: any;
groupIdInput?: any;
"@groupIdInput"?: any;
artifactIdInput?: any;
"@artifactIdInput"?: any;
versionInput?: any;
"@versionInput"?: any;
actions?: any;
"@actions"?: any;
actionCancel?: any;
"@actionCancel"?: any;
actionValidate?: any;
"@actionValidate"?: any;

}

export class ChangeGavCard {
    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: ChangeGavCardDto): string {
        ChangeGavCard.ensureLoaded();

        return tardigrade.tardigradeEngine.buildHtml("ChangeGavCard", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:ChangeGavCardDto): HTMLElement {
        return tardigrade.createElement(ChangeGavCard.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:ChangeGavCardDto): ChangeGavCard {
        let element = ChangeGavCard.element(dto);
        return new ChangeGavCard(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): ChangeGavCard {
        return new ChangeGavCard(element);
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

    /** Returns the html element corresponding to the 'groupId' point */
groupId(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "groupId": 0 });
}
/** Returns true if the part named 'groupId' with id 'groupId' was hit */
                groupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("groupId" in location));
                        }
/** Returns the html element corresponding to the 'artifactId' point */
artifactId(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "artifactId": 0 });
}
/** Returns true if the part named 'artifactId' with id 'artifactId' was hit */
                artifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("artifactId" in location));
                        }
/** Returns the html element corresponding to the 'version' point */
version(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "version": 0 });
}
/** Returns true if the part named 'version' with id 'version' was hit */
                versionHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("version" in location));
                        }
/** Returns the html element corresponding to the 'groupIdInput' point */
groupIdInput(): HTMLInputElement {
return <HTMLInputElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "groupIdInput": 0 });
}
/** Returns true if the part named 'groupIdInput' with id 'groupIdInput' was hit */
                groupIdInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("groupIdInput" in location));
                        }
/** Returns the html element corresponding to the 'artifactIdInput' point */
artifactIdInput(): HTMLInputElement {
return <HTMLInputElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "artifactIdInput": 0 });
}
/** Returns true if the part named 'artifactIdInput' with id 'artifactIdInput' was hit */
                artifactIdInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("artifactIdInput" in location));
                        }
/** Returns the html element corresponding to the 'versionInput' point */
versionInput(): HTMLInputElement {
return <HTMLInputElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "versionInput": 0 });
}
/** Returns true if the part named 'versionInput' with id 'versionInput' was hit */
                versionInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("versionInput" in location));
                        }
/** Returns the html element corresponding to the 'actions' point */
actions(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "actions": 0 });
}
/** Returns true if the part named 'actions' with id 'actions' was hit */
                actionsHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("actions" in location));
                        }
/** Returns the html element corresponding to the 'actionCancel' point */
actionCancel(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "actionCancel": 0 });
}
/** Returns true if the part named 'actionCancel' with id 'actionCancel' was hit */
                actionCancelHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("actionCancel" in location));
                        }
/** Returns the html element corresponding to the 'actionValidate' point */
actionValidate(): HTMLElement {
return <HTMLElement>tardigrade.tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "actionValidate": 0 });
}
/** Returns true if the part named 'actionValidate' with id 'actionValidate' was hit */
                actionValidateHit(hitTest:HTMLElement): boolean {
                        let location = tardigrade.tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("actionValidate" in location));
                        }

    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(ChangeGavCard.loaded)
            return;
        ChangeGavCard.loaded = true;

        BaseCard.ensureLoaded();

        tardigrade.tardigradeEngine.addTemplate("ChangeGavCard", {t:[null, 0, [""], "BaseCard", {}, {"title": [null, {}, ["Changing&nbsp;", {e:["groupId", 0, [""], "span", {}, []]}, ":", {e:["artifactId", 0, [""], "span", {}, []]}, ":", {e:["version", 0, [""], "span", {}, []]}]], "content": [null, {}, ["You can change this GAV and all projects linked to it will be updated. By now,", {e:[null, 0, [""], "b", {}, ["NO CHANGE IS APPLIED"]]}, "until            you go in the Change tab and validate.", {e:[null, 0, [""], "br", {}, []]}, "Enter the new coordinates for this GAV :", {e:[null, 0, [""], "br", {}, []]}, {e:[null, 0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [{e:["groupIdInput", 0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "groupId"}, []]}, {e:[null, 0, [""], "label", {"class": "mdl-textfield__label", "for": "groupId"}, ["groupId"]]}]]}, {e:[null, 0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [{e:["artifactIdInput", 0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "artifactId"}, []]}, {e:[null, 0, [""], "label", {"class": "mdl-textfield__label", "for": "artifactId"}, ["artifactId"]]}]]}, {e:[null, 0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [{e:["versionInput", 0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "version"}, []]}, {e:[null, 0, [""], "label", {"class": "mdl-textfield__label", "for": "version"}, ["version"]]}]]}]], "actions": ["actions", {}, [{e:["actionCancel", 0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, ["Cancel"]]}, {e:["actionValidate", 0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, ["Ok, store the change"]]}]]}]});
    }
}