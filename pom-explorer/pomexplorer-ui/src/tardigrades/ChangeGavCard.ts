"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { baseCardTemplate, BaseCardTemplateElement } from "./BaseCard";

export interface ChangeGavCardTemplateDto {
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

export interface ChangeGavCardTemplateElement {
    _root(): HTMLElement;
    groupId(): HTMLElement;
artifactId(): HTMLElement;
version(): HTMLElement;
groupIdInput(): HTMLInputElement;
artifactIdInput(): HTMLInputElement;
versionInput(): HTMLInputElement;
actions(): HTMLElement;
actionCancel(): HTMLElement;
actionValidate(): HTMLElement;
}

class ChangeGavCardTemplate {
    ensureLoaded() {
    }

    constructor() {
        baseCardTemplate.ensureLoaded();

        tardigradeEngine.addTemplate("ChangeGavCard", new TemplateNode(null, <Cardinal>0, [""], "BaseCard", {}, {"title": new PointInfo(null, {}, [new TextNode("Changing GAV of"), new ElementNode("groupId", <Cardinal>0, [""], "span", {}, []), new TextNode(":"), new ElementNode("artifactId", <Cardinal>0, [""], "span", {}, []), new TextNode(":"), new ElementNode("version", <Cardinal>0, [""], "span", {}, [])]), "content": new PointInfo(null, {}, [new TextNode("You can change this GAV and all projects linked to it will be updated. By now,"), new ElementNode(null, <Cardinal>0, [""], "b", {}, [new TextNode("NO CHANGE IS APPLIED")]), new TextNode("until            you go in the Change tab and validate."), new ElementNode(null, <Cardinal>0, [""], "br", {}, []), new TextNode("Enter the new coordinates for this GAV :"), new ElementNode(null, <Cardinal>0, [""], "br", {}, []), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [new ElementNode("groupIdInput", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "groupId"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "groupId"}, [new TextNode("groupId")])]), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [new ElementNode("artifactIdInput", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "artifactId"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "artifactId"}, [new TextNode("artifactId")])]), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label", "style": "display:block;"}, [new ElementNode("versionInput", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "version"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "version"}, [new TextNode("version")])])]), "actions": new PointInfo("actions", {}, [new ElementNode("actionCancel", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Cancel")]), new ElementNode("actionValidate", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Ok, store the change")])])}));
    }

    buildHtml(dto: ChangeGavCardTemplateDto) {
        return tardigradeEngine.buildHtml("ChangeGavCard", dto);
    }

    buildElement(dto: ChangeGavCardTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: ChangeGavCardTemplateDto): ChangeGavCardTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): ChangeGavCardTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            groupId(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "groupId": 0 });
},
artifactId(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "artifactId": 0 });
},
version(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "version": 0 });
},
groupIdInput(): HTMLInputElement{
return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "groupIdInput": 0 });
},
artifactIdInput(): HTMLInputElement{
return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "artifactIdInput": 0 });
},
versionInput(): HTMLInputElement{
return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "versionInput": 0 });
},
actions(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "actions": 0 });
},
actionCancel(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "actionCancel": 0 });
},
actionValidate(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ChangeGavCard", { "actionValidate": 0 });
}
        };

        return domlet;
    }
}

export var changeGavCardTemplate = new ChangeGavCardTemplate();