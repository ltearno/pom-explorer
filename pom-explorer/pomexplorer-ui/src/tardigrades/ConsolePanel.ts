"use strict";

import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

export interface ConsolePanelTemplateDto {
}

export interface ConsolePanelTemplateElement {
    _root(): HTMLElement;
    input(): HTMLInputElement;
    output(): HTMLDivElement;
}

export class ConsolePanelTemplate {
    constructor() {
        tardigradeEngine.addTemplate("ConsolePanel", tardigradeParser.parseTemplate(`
<div class="console-panel">
    <div x-id="output" class='console-output'></div>
    <form action="#" class='console-input'>
        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
            <input x-id="input" class="mdl-textfield__input" type="text" id="sample3">
            <label class="mdl-textfield__label" for="sample3">enter a command, or just "?" to get help</label>
        </div>
    </form>
</div>
`));
    }

    buildHtml(dto: ConsolePanelTemplateDto) {
        return tardigradeEngine.buildHtml("ConsolePanel", dto);
    }

    buildElement(dto: ConsolePanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): ConsolePanelTemplateElement {
        return {
            _root(): HTMLDivElement {
                return <HTMLDivElement>rootElement;
            },

            input() {
                return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "input": 0 });
            },

            output() {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "ConsolePanel", { "output": 0 });
            }
        };
    }
}

export var consolePanelTemplate = new ConsolePanelTemplate();