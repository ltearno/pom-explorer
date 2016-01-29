"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { baseCardTemplate, BaseCardTemplateElement } from "./BaseCard";
import { gavTemplate, GavTemplateElement } from "./Gav";

export interface CardTemplateDto {
    _root?: string;
    gav?: any;
"@gav"?: any;
gavGroupId?: any;
"@gavGroupId"?: any;
gavArtifactId?: any;
"@gavArtifactId"?: any;
gavVersion?: any;
"@gavVersion"?: any;
edit?: any;
"@edit"?: any;
content?: any;
"@content"?: any;
details?: any;
"@details"?: any;
actions?: any;
"@actions"?: any;
actionDetails?: any;
"@actionDetails"?: any;
actionBuild?: any;
"@actionBuild"?: any;

}

export interface CardTemplateElement {
    _root(): HTMLElement;
    gav(): HTMLElement;
gavDomlet(): GavTemplateElement;
gavGroupId(): HTMLElement;
gavArtifactId(): HTMLElement;
gavVersion(): HTMLElement;
edit(): HTMLElement;
content(): HTMLElement;
details(): HTMLElement;
actions(): HTMLElement;
actionDetails(): HTMLElement;
actionBuild(): HTMLElement;
}

class CardTemplate {
    ensureLoaded() {
    }

    constructor() {
        baseCardTemplate.ensureLoaded();
gavTemplate.ensureLoaded();

        tardigradeEngine.addTemplate("Card", new TemplateNode(null, <Cardinal>0, [""], "BaseCard", {}, {"title": new PointInfo(null, {}, [new TemplateNode("gav", <Cardinal>0, ["export"], "Gav", {"class": "mdl-card__title-text"}, {"groupId": new PointInfo("gavGroupId", {}, []), "artifactId": new PointInfo("gavArtifactId", {}, []), "version": new PointInfo("gavVersion", {}, [])}), new ElementNode("edit", <Cardinal>0, [""], "i", {"class": "material-icons"}, [new TextNode("mode_edit")])]), "content": new PointInfo("content", {}, []), "details": new PointInfo("details", {}, []), "actions": new PointInfo("actions", {}, [new ElementNode("actionDetails", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Details")]), new ElementNode("actionBuild", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Build")])])}));
    }

    buildHtml(dto: CardTemplateDto) {
        return tardigradeEngine.buildHtml("Card", dto);
    }

    buildElement(dto: CardTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: CardTemplateDto): CardTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): CardTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            gav(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gav": 0 });
},
gavDomlet(): GavTemplateElement {
let element = domlet.gav();
return gavTemplate.of(element);
},
gavGroupId(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavGroupId": 0 });
},
gavArtifactId(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavArtifactId": 0 });
},
gavVersion(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavVersion": 0 });
},
edit(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "edit": 0 });
},
content(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "content": 0 });
},
details(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "details": 0 });
},
actions(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actions": 0 });
},
actionDetails(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionDetails": 0 });
},
actionBuild(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionBuild": 0 });
}
        };

        return domlet;
    }
}

export var cardTemplate = new CardTemplate();