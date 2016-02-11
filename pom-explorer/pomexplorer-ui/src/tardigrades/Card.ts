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
content?: any;
"@content"?: any;
details?: any;
"@details"?: any;
actions?: any;
"@actions"?: any;
actionDetails?: any;
"@actionDetails"?: any;
edit?: any;
"@edit"?: any;

}

export interface CardTemplateElement {
    _root(): HTMLElement;
    // returns the previous data
    setUserData(data:any):any;
    getUserData():any;
    gav(): HTMLElement;
gavDomlet(): GavTemplateElement;
gavHitDomlet(hitElement: HTMLElement): GavTemplateElement;
gavHit(hitTest:HTMLElement): boolean;
gavGroupId(): HTMLElement;
gavGroupIdHit(hitTest:HTMLElement): boolean;
gavArtifactId(): HTMLElement;
gavArtifactIdHit(hitTest:HTMLElement): boolean;
gavVersion(): HTMLElement;
gavVersionHit(hitTest:HTMLElement): boolean;
content(): HTMLElement;
contentHit(hitTest:HTMLElement): boolean;
details(): HTMLElement;
detailsHit(hitTest:HTMLElement): boolean;
actions(): HTMLElement;
actionsHit(hitTest:HTMLElement): boolean;
actionDetails(): HTMLElement;
actionDetailsHit(hitTest:HTMLElement): boolean;
edit(): HTMLElement;
editHit(hitTest:HTMLElement): boolean;
}

class CardTemplate {
    ensureLoaded() {
    }

    constructor() {
        baseCardTemplate.ensureLoaded();
gavTemplate.ensureLoaded();

        tardigradeEngine.addTemplate("Card", new TemplateNode(null, <Cardinal>0, [""], "BaseCard", {}, {"title": new PointInfo(null, {}, [new TemplateNode("gav", <Cardinal>0, ["export"], "Gav", {"class": "mdl-card__title-text"}, {"groupId": new PointInfo("gavGroupId", {}, []), "artifactId": new PointInfo("gavArtifactId", {}, []), "version": new PointInfo("gavVersion", {}, [])})]), "content": new PointInfo("content", {}, []), "details": new PointInfo("details", {}, []), "actions": new PointInfo("actions", {}, [new ElementNode("actionDetails", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Details")]), new ElementNode("edit", <Cardinal>0, [""], "button", {"class": "mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"}, [new ElementNode(null, <Cardinal>0, [""], "i", {"class": "material-icons"}, [new TextNode("mode_edit")])])])}));
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

            setUserData(data:any):any {
                let old = (rootElement as any)._tardigradeUserData || null;
                (rootElement as any)._tardigradeUserData = data;
                return old;
            },

            getUserData():any {
                return (rootElement as any)._tardigradeUserData || null;
            },

            gav(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gav": 0 });
},
gavDomlet(): GavTemplateElement {
let element = domlet.gav();
return gavTemplate.of(element);
},
gavHitDomlet(hitElement: HTMLElement): GavTemplateElement {
let location = tardigradeEngine.getLocation(rootElement, "Card", hitElement);
if(location==null) return null;
return domlet.gavDomlet();
},
gavHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("gav" in location));
                        },
gavGroupId(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavGroupId": 0 });
},
gavGroupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("gavGroupId" in location));
                        },
gavArtifactId(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavArtifactId": 0 });
},
gavArtifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("gavArtifactId" in location));
                        },
gavVersion(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavVersion": 0 });
},
gavVersionHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("gavVersion" in location));
                        },
content(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "content": 0 });
},
contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("content" in location));
                        },
details(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "details": 0 });
},
detailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("details" in location));
                        },
actions(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actions": 0 });
},
actionsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("actions" in location));
                        },
actionDetails(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionDetails": 0 });
},
actionDetailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("actionDetails" in location));
                        },
edit(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "edit": 0 });
},
editHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Card", hitTest);
                        return (location != null && ("edit" in location));
                        }
        };

        return domlet;
    }
}

export var cardTemplate = new CardTemplate();