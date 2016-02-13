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

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(Card.loaded)
            return;
        Card.loaded = true;

        BaseCard.ensureLoaded();
Gav.ensureLoaded();

        tardigradeEngine.addTemplate("Card", new TemplateNode(null, <Cardinal>0, [""], "BaseCard", {}, {"title": new PointInfo(null, {}, [new TemplateNode("gav", <Cardinal>0, ["export"], "Gav", {"class": "mdl-card__title-text"}, {"groupId": new PointInfo("gavGroupId", {}, []), "artifactId": new PointInfo("gavArtifactId", {}, []), "version": new PointInfo("gavVersion", {}, [])})]), "content": new PointInfo("content", {}, []), "details": new PointInfo("details", {}, []), "actions": new PointInfo("actions", {}, [new ElementNode("actionDetails", <Cardinal>0, [""], "a", {"class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"}, [new TextNode("Details")]), new ElementNode("edit", <Cardinal>0, [""], "button", {"class": "mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"}, [new ElementNode(null, <Cardinal>0, [""], "i", {"class": "material-icons"}, [new TextNode("mode_edit")])])])}));
    }

    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: CardDto): string {
        Card.ensureLoaded();

        return tardigradeEngine.buildHtml("Card", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:CardDto): HTMLElement {
        return createElement(Card.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:CardDto): Card {
        let element = Card.element(dto);
        return new Card(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): Card {
        return new Card(element);
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

    /** Returns the html element corresponding to the 'gav' point */
gav(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "gav": 0 });
}
/** Returns the template instance for the point 'gav' with id 'gav' */
gavDomlet(): Gav {
let element = this.gav();
return Gav.of(element);
}
/** Returns the 'gav' with id 'gav' template instance that is hit by the hitElement */
gavHitDomlet(hitElement: HTMLElement): Gav {
let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitElement);
if(location==null) return null;
return this.gavDomlet();
}
/** Returns true if the part named 'gav' with id 'gav' was hit */
                gavHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("gav" in location));
                        }
/** Returns the html element corresponding to the 'gavGroupId' point */
gavGroupId(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "gavGroupId": 0 });
}
/** Returns true if the part named 'gavGroupId' with id 'gavGroupId' was hit */
                gavGroupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("gavGroupId" in location));
                        }
/** Returns the html element corresponding to the 'gavArtifactId' point */
gavArtifactId(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "gavArtifactId": 0 });
}
/** Returns true if the part named 'gavArtifactId' with id 'gavArtifactId' was hit */
                gavArtifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("gavArtifactId" in location));
                        }
/** Returns the html element corresponding to the 'gavVersion' point */
gavVersion(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "gavVersion": 0 });
}
/** Returns true if the part named 'gavVersion' with id 'gavVersion' was hit */
                gavVersionHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("gavVersion" in location));
                        }
/** Returns the html element corresponding to the 'content' point */
content(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "content": 0 });
}
/** Returns true if the part named 'content' with id 'content' was hit */
                contentHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("content" in location));
                        }
/** Returns the html element corresponding to the 'details' point */
details(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "details": 0 });
}
/** Returns true if the part named 'details' with id 'details' was hit */
                detailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("details" in location));
                        }
/** Returns the html element corresponding to the 'actions' point */
actions(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "actions": 0 });
}
/** Returns true if the part named 'actions' with id 'actions' was hit */
                actionsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("actions" in location));
                        }
/** Returns the html element corresponding to the 'actionDetails' point */
actionDetails(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "actionDetails": 0 });
}
/** Returns true if the part named 'actionDetails' with id 'actionDetails' was hit */
                actionDetailsHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("actionDetails" in location));
                        }
/** Returns the html element corresponding to the 'edit' point */
edit(): HTMLElement {
return <HTMLElement>tardigradeEngine.getPoint(this.rootElement, "Card", { "edit": 0 });
}
/** Returns true if the part named 'edit' with id 'edit' was hit */
                editHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Card", hitTest);
                        return (location != null && ("edit" in location));
                        }
}