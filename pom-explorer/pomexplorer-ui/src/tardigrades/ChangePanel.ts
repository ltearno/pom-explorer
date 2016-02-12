"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface ChangePanelDto {
    _root?: string;
    graphChanges?: any;
"@graphChanges"?: any;
projectChanges?: any;
"@projectChanges"?: any;

}

export class ChangePanel {
    private static loaded = false;

    static ensureLoaded() {
        if(ChangePanel.loaded)
            return;
        ChangePanel.loaded = true;

        

        tardigradeEngine.addTemplate("ChangePanel", new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode(null, <Cardinal>0, [""], "h2", {}, [new TextNode("Graph changes")]), new ElementNode("graphChanges", <Cardinal>0, [""], "div", {}, [])]), new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode(null, <Cardinal>0, [""], "h2", {}, [new TextNode("Project changes")]), new ElementNode("projectChanges", <Cardinal>0, [""], "div", {}, [])])]));
    }

    static html(dto: ChangePanelDto): string {
        ChangePanel.ensureLoaded();

        return tardigradeEngine.buildHtml("ChangePanel", dto);
    }

    static element(dto:ChangePanelDto): HTMLElement {
        return createElement(ChangePanel.html(dto));
    }

    static create(dto:ChangePanelDto): ChangePanel {
        let element = ChangePanel.element(dto);
        return new ChangePanel(element);
    }

    static of(element: HTMLElement): ChangePanel {
        return new ChangePanel(element);
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

    graphChanges(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "ChangePanel", { "graphChanges": 0 });
}
graphChangesHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangePanel", hitTest);
                        return (location != null && ("graphChanges" in location));
                        }
projectChanges(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "ChangePanel", { "projectChanges": 0 });
}
projectChangesHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ChangePanel", hitTest);
                        return (location != null && ("projectChanges" in location));
                        }
}