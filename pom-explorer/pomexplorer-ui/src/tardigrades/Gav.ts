"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface GavDto {
    _root?: string;
    groupId?: any;
"@groupId"?: any;
artifactId?: any;
"@artifactId"?: any;
version?: any;
"@version"?: any;

}

export class Gav {
    private static loaded = false;

    /** This method should not be called by your application ! */
    static ensureLoaded() {
        if(Gav.loaded)
            return;
        Gav.loaded = true;

        

        tardigradeEngine.addTemplate("Gav", new ElementNode(null, <Cardinal>0, [""], "h2", {"class": "mdl-card__title-text"}, [new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode("groupId", <Cardinal>0, [""], "div", {}, []), new ElementNode("artifactId", <Cardinal>0, [""], "div", {}, []), new ElementNode("version", <Cardinal>0, [""], "div", {}, [])])]));
    }

    /** Builds an HTML string according to the dto you provide
     * @return The built HTML string */
    static html(dto: GavDto): string {
        Gav.ensureLoaded();

        return tardigradeEngine.buildHtml("Gav", dto);
    }

    /** Builds an HTMLElement according to the dto you provide
     * @return The built HTMLElement */
    static element(dto:GavDto): HTMLElement {
        return createElement(Gav.html(dto));
    }

    /** Builds a template instance according to the dto you provide.
     * This instance holds its root HTMLElement for you.
     * @return The built template instance */
    static create(dto:GavDto): Gav {
        let element = Gav.element(dto);
        return new Gav(element);
    }

    /** Builds a template instance from the HTMLElement you provide.
     * @param {HTMLElement} The HTML element that corresponds to this template
     * @return The built template instance */
    static of(element: HTMLElement): Gav {
        return new Gav(element);
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
groupId(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Gav", { "groupId": 0 });
}
/** Returns true if the part named 'groupId' with id 'groupId' was hit */
                groupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("groupId" in location));
                        }
/** Returns the html element corresponding to the 'artifactId' point */
artifactId(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Gav", { "artifactId": 0 });
}
/** Returns true if the part named 'artifactId' with id 'artifactId' was hit */
                artifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("artifactId" in location));
                        }
/** Returns the html element corresponding to the 'version' point */
version(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Gav", { "version": 0 });
}
/** Returns true if the part named 'version' with id 'version' was hit */
                versionHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("version" in location));
                        }
}