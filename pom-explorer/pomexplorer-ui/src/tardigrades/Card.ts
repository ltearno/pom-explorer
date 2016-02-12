"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { BaseCard } from "./BaseCard";
import { Gav } from "./Gav";

export interface CardDto {
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

export class Card {
    private static loaded = false;

    static ensureLoaded() {
        if(Card.loaded)
            return;
        Card.loaded = true;

        BaseCard.ensureLoaded();
Gav.ensureLoaded();

        tardigradeEngine.addTemplate("Card", new TemplateNode(null, <Cardinal>0, [""], "BaseCard", {}, {"title": new PointInfo(null, {}, [new TemplateNode("gav", <Cardinal>0, ["export"], "Gav", {"class": "mdl-card__title-text"}, {"groupId": new PointInfo("gavGroupId", {}, []), "artifactId": new PointInfo("gavArtifactId", {}, []), "version": new PointInfo("gavVersion", {}, [])})]), "content": new PointInfo("content", {}, []), "details": new PointInfo("details", {}, []), "actions": new PointInfo("actions", {}, [new ElementNode("actionDetails", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Details")]), new ElementNode("edit", <Cardinal>0, [""], "button", {"class": "mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"}, [new ElementNode(null, <Cardinal>0, [""], "i", {"class": "material-icons"}, [new TextNode("mode_edit")])])])}));
    }

    static html(dto: CardDto): string {
        Card.ensureLoaded();

        return tardigradeEngine.buildHtml("Card", dto);
    }

    static element(dto:CardDto): HTMLElement {
        return createElement(Card.html(dto));
    }

    static create(dto:CardDto): Card {
        let element = Card.element(dto);
        return new Card(element);
    }

    static of(element: HTMLElement): Card {
        return new Card(element);
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

    gav(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "gav": 0 });
}
gavDomlet(): Gav {
let element = this.gav();
return Gav.of(element);
}
gavHitDomlet(hitElement: HTMLElement): Gav {
let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitElement);
if(location==null) return null;
return this.gavDomlet();
}
gavHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("gav" in location));
                        }
gavGroupId(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "gavGroupId": 0 });
}
gavGroupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("gavGroupId" in location));
                        }
gavArtifactId(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "gavArtifactId": 0 });
}
gavArtifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("gavArtifactId" in location));
                        }
gavVersion(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "gavVersion": 0 });
}
gavVersionHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("gavVersion" in location));
                        }
content(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "content": 0 });
}
contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("content" in location));
                        }
details(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "details": 0 });
}
detailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("details" in location));
                        }
actions(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "actions": 0 });
}
actionsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("actions" in location));
                        }
actionDetails(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "actionDetails": 0 });
}
actionDetailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("actionDetails" in location));
                        }
edit(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "edit": 0 });
}
editHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("edit" in location));
                        }
}