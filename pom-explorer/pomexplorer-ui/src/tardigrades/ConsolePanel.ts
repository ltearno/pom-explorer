"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
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
        

        tardigradeEngine.addTemplate("ConsolePanel", tardigradeParser.parseTemplate(`<html>
<body>
<div class="console-panel">
    <div x-id="output" class='console-output'></div>
    <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
        <input x-id="input" class="mdl-textfield__input" type="text" id="sample3">
        <label class="mdl-textfield__label" for="sample3">enter a command, or just "?" to get help</label>
    </div>
</div>
</body>
</html>`));
    }

    buildHtml(dto: ConsolePanelTemplateDto) {
        return tardigradeEngine.buildHtml("ConsolePanel", dto);
    }

    buildElement(dto: ConsolePanelTemplateDto) {
        return createElement(this.buildHtml(dto));
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