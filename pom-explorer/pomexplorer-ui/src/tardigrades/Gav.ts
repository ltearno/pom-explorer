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

    static ensureLoaded() {
        if(Gav.loaded)
            return;
        Gav.loaded = true;

        

        tardigradeEngine.addTemplate("Gav", new ElementNode(null, <Cardinal>0, [""], "h2", {"class": "mdl-card__title-text"}, [new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode("groupId", <Cardinal>0, [""], "div", {}, []), new ElementNode("artifactId", <Cardinal>0, [""], "div", {}, []), new ElementNode("version", <Cardinal>0, [""], "div", {}, [])])]));
    }

    static html(dto: GavDto): string {
        Gav.ensureLoaded();

        return tardigradeEngine.buildHtml("Gav", dto);
    }

    static element(dto:GavDto): HTMLElement {
        return createElement(Gav.html(dto));
    }

    static create(dto:GavDto): Gav {
        let element = Gav.element(dto);
        return new Gav(element);
    }

    static of(element: HTMLElement): Gav {
        return new Gav(element);
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

    groupId(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Gav", { "groupId": 0 });
}
groupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("groupId" in location));
                        }
artifactId(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Gav", { "artifactId": 0 });
}
artifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("artifactId" in location));
                        }
version(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "Gav", { "version": 0 });
}
versionHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "Gav", hitTest);
                        return (location != null && ("version" in location));
                        }
}