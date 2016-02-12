"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { BaseCard } from "./BaseCard";

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
    private static loaded = false;

    static ensureLoaded() {
        if(ChangeGavCard.loaded)
            return;
        ChangeGavCard.loaded = true;

        BaseCard.ensureLoaded();

        tardigradeEngine.addTemplate("ChangeGavCard", new TemplateNode(null, <Cardinal>0, [""], "BaseCard", {}, {"title": new PointInfo(null, {}, [new TextNode("Changing&nbsp;"), new ElementNode("groupId", <Cardinal>0, [""], "span", {}, []), new TextNode(":"), new ElementNode("artifactId", <Cardinal>0, [""], "span", {}, []), new TextNode(":"), new ElementNode("version", <Cardinal>0, [""], "span", {}, [])]), "content": new PointInfo(null, {}, [new TextNode("You can change this GAV and all projects linked to it will be updated. By now,"), new ElementNode(null, <Cardinal>0, [""], "b", {}, [new TextNode("NO CHANGE IS APPLIED")]), new TextNode("until            you go in the Change tab and validate."), new ElementNode(null, <Cardinal>0, [""], "br", {}, []), new TextNode("Enter the new coordinates for this GAV :"), new ElementNode(null, <Cardinal>0, [""], "br", {}, []), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [new ElementNode("groupIdInput", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "groupId"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "groupId"}, [new TextNode("groupId")])]), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [new ElementNode("artifactIdInput", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "artifactId"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "artifactId"}, [new TextNode("artifactId")])]), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [new ElementNode("versionInput", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "version"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "version"}, [new TextNode("version")])])]), "actions": new PointInfo("actions", {}, [new ElementNode("actionCancel", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Cancel")]), new ElementNode("actionValidate", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Ok, store the change")])])}));
    }

    static html(dto: ChangeGavCardDto): string {
        ChangeGavCard.ensureLoaded();

        return tardigradeEngine.buildHtml("ChangeGavCard", dto);
    }

    static element(dto:ChangeGavCardDto): HTMLElement {
        return createElement(ChangeGavCard.html(dto));
    }

    static create(dto:ChangeGavCardDto): ChangeGavCard {
        let element = ChangeGavCard.element(dto);
        return new ChangeGavCard(element);
    }

    static of(element: HTMLElement): ChangeGavCard {
        return new ChangeGavCard(element);
    }

    constructor(private rootElement: HTMLElement) {}

    rootHtmlElement(): HTMLElement { return this.rootElement; }

    setUserData(data:any): any {
        let old = (this.rootElement as any)._tardigradeUserData || undefined;
        (this.rootElement as any)._tardigradeUserData = data;
        return old;
    }

    getUserData():any {
        return (this.rootElement as any)._tardigradeUserData || undefined;
    }

    groupId(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "groupId": 0 });
}
groupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("groupId" in location));
                        }
artifactId(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "artifactId": 0 });
}
artifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("artifactId" in location));
                        }
version(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "version": 0 });
}
versionHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("version" in location));
                        }
groupIdInput(): HTMLInputElement {
return <HTMLInputElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "groupIdInput": 0 });
}
groupIdInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("groupIdInput" in location));
                        }
artifactIdInput(): HTMLInputElement {
return <HTMLInputElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "artifactIdInput": 0 });
}
artifactIdInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("artifactIdInput" in location));
                        }
versionInput(): HTMLInputElement {
return <HTMLInputElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "versionInput": 0 });
}
versionInputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("versionInput" in location));
                        }
actions(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "actions": 0 });
}
actionsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("actions" in location));
                        }
actionCancel(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "actionCancel": 0 });
}
actionCancelHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("actionCancel" in location));
                        }
actionValidate(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "ChangeGavCard", { "actionValidate": 0 });
}
actionValidateHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangeGavCard", hitTest);
                        return (location != null && ("actionValidate" in location));
                        }
}