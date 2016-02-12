"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface ConsolePanelDto {
    _root?: string;
    output?: any;
"@output"?: any;
input?: any;
"@input"?: any;

}

export class ConsolePanel {
    private static loaded = false;

    static ensureLoaded() {
        if(ConsolePanel.loaded)
            return;
        ConsolePanel.loaded = true;

        

        tardigradeEngine.addTemplate("ConsolePanel", new ElementNode(null, <Cardinal>0, [""], "div", {"class": "console-panel"}, [new ElementNode("output", <Cardinal>0, [""], "div", {"class": "console-output"}, []), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label"}, [new ElementNode("input", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "sample3"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "sample3"}, [new TextNode("enter a command, or just \"?\" to get help")])])]));
    }

    static html(dto: ConsolePanelDto): string {
        ConsolePanel.ensureLoaded();

        return tardigradeEngine.buildHtml("ConsolePanel", dto);
    }

    static element(dto:ConsolePanelDto): HTMLElement {
        return createElement(ConsolePanel.html(dto));
    }

    static create(dto:ConsolePanelDto): ConsolePanel {
        let element = ConsolePanel.element(dto);
        return new ConsolePanel(element);
    }

    static of(element: HTMLElement): ConsolePanel {
        return new ConsolePanel(element);
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

    output(): HTMLDivElement {
return <HTMLDivElement>tardigradeEngine.getPoint(this.rootElement, "ConsolePanel", { "output": 0 });
}
outputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ConsolePanel", hitTest);
                        return (location != null && ("output" in location));
                        }
input(): HTMLInputElement {
return <HTMLInputElement>tardigradeEngine.getPoint(this.rootElement, "ConsolePanel", { "input": 0 });
}
inputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "ConsolePanel", hitTest);
                        return (location != null && ("input" in location));
                        }
}