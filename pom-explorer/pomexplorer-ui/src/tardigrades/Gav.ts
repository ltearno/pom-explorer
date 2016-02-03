"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface GavTemplateDto {
    _root?: string;
    groupId?: any;
"@groupId"?: any;
artifactId?: any;
"@artifactId"?: any;
version?: any;
"@version"?: any;

}

export interface GavTemplateElement {
    _root(): HTMLElement;
    // returns the previous data
    setUserData(data:any):any;
    getUserData():any;
    groupId(): HTMLDivElement;
groupIdHit(hitTest:HTMLElement): boolean;
artifactId(): HTMLDivElement;
artifactIdHit(hitTest:HTMLElement): boolean;
version(): HTMLDivElement;
versionHit(hitTest:HTMLElement): boolean;
}

class GavTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("Gav", new ElementNode(null, <Cardinal>0, [""], "h2", {"class": "mdl-card__title-text"}, [new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode("groupId", <Cardinal>0, [""], "div", {}, []), new ElementNode("artifactId", <Cardinal>0, [""], "div", {}, []), new ElementNode("version", <Cardinal>0, [""], "div", {}, [])])]));
    }

    buildHtml(dto: GavTemplateDto) {
        return tardigradeEngine.buildHtml("Gav", dto);
    }

    buildElement(dto: GavTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: GavTemplateDto): GavTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): GavTemplateElement {
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

            groupId(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Gav", { "groupId": 0 });
},
groupIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Gav", hitTest);
                        return (location != null && ("groupId" in location));
                        },
artifactId(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Gav", { "artifactId": 0 });
},
artifactIdHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Gav", hitTest);
                        return (location != null && ("artifactId" in location));
                        },
version(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Gav", { "version": 0 });
},
versionHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "Gav", hitTest);
                        return (location != null && ("version" in location));
                        }
        };

        return domlet;
    }
}

export var gavTemplate = new GavTemplate();