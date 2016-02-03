"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface ConsolePanelTemplateDto {
    _root?: string;
    output?: any;
"@output"?: any;
input?: any;
"@input"?: any;

}

export interface ConsolePanelTemplateElement {
    _root(): HTMLElement;
    // returns the previous data
    setUserData(data:any):any;
    getUserData():any;
    output(): HTMLDivElement;
outputHit(hitTest:HTMLElement): boolean;
input(): HTMLInputElement;
inputHit(hitTest:HTMLElement): boolean;
}

class ConsolePanelTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("ConsolePanel", new ElementNode(null, <Cardinal>0, [""], "div", {"class": "console-panel"}, [new ElementNode("output", <Cardinal>0, [""], "div", {"class": "console-output"}, []), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label"}, [new ElementNode("input", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "sample3"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "sample3"}, [new TextNode("enter a command, or just \"?\" to get help")])])]));
    }

    buildHtml(dto: ConsolePanelTemplateDto) {
        return tardigradeEngine.buildHtml("ConsolePanel", dto);
    }

    buildElement(dto: ConsolePanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: ConsolePanelTemplateDto): ConsolePanelTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): ConsolePanelTemplateElement {
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

            output(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "output": 0 });
},
outputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "ConsolePanel", hitTest);
                        return (location != null && ("output" in location));
                        },
input(): HTMLInputElement{
return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "input": 0 });
},
inputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(rootElement, "ConsolePanel", hitTest);
                        return (location != null && ("input" in location));
                        }
        };

        return domlet;
    }
}

export var consolePanelTemplate = new ConsolePanelTemplate();