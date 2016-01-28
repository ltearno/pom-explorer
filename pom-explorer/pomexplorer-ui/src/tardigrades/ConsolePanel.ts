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
    output(): HTMLDivElement;
input(): HTMLInputElement;
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

            output(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "output": 0 });
},
input(): HTMLInputElement{
return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "input": 0 });
}
        };

        return domlet;
    }
}

export var consolePanelTemplate = new ConsolePanelTemplate();