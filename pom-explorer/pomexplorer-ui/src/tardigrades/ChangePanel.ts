"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface ChangePanelTemplateDto {
    _root?: string;
    graphChanges?: any;
"@graphChanges"?: any;
projectChanges?: any;
"@projectChanges"?: any;

}

export interface ChangePanelTemplateElement {
    _root(): HTMLElement;
    // returns the previous data
    setUserData(data:any):any;
    getUserData():any;
    graphChanges(): HTMLDivElement;
graphChangesHit(hitTest:HTMLElement): boolean;
projectChanges(): HTMLDivElement;
projectChangesHit(hitTest:HTMLElement): boolean;
}

class ChangePanelTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("ChangePanel", new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode(null, <Cardinal>0, [""], "h2", {}, [new TextNode("Graph changes")]), new ElementNode("graphChanges", <Cardinal>0, [""], "div", {}, [])]), new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode(null, <Cardinal>0, [""], "h2", {}, [new TextNode("Project changes")]), new ElementNode("projectChanges", <Cardinal>0, [""], "div", {}, [])])]));
    }

    buildHtml(dto: ChangePanelTemplateDto) {
        return tardigradeEngine.buildHtml("ChangePanel", dto);
    }

    buildElement(dto: ChangePanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: ChangePanelTemplateDto): ChangePanelTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): ChangePanelTemplateElement {
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

            graphChanges(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "ChangePanel", { "graphChanges": 0 });
},
graphChangesHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "ChangePanel", hitTest);
                        return (location != null && ("graphChanges" in location));
                        },
projectChanges(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "ChangePanel", { "projectChanges": 0 });
},
projectChangesHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "ChangePanel", hitTest);
                        return (location != null && ("projectChanges" in location));
                        }
        };

        return domlet;
    }
}

export var changePanelTemplate = new ChangePanelTemplate();