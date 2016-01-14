"use strict";

import { tardigradeEngine } from "../node_modules/tardigrade/target/engine/engine";
import { createElement, domChain, indexOf } from "../node_modules/tardigrade/target/engine/runtime";

interface SearchPanelTemplateDto {
    input?: string;
}

interface SearchPanelTemplateElement {
    _root(): HTMLDivElement;
    input(): HTMLInputElement;
}

class SearchPanelTemplate {
    ensureLoaded() {
    }
    
    constructor() {
        tardigradeEngine.addTemplate("SearchPanel", `
<form action="#">
  <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
    <input x-id="input" class="mdl-textfield__input" type="text" id="searchBox">
    <label class="mdl-textfield__label" for="searchBox">Project search...</label>
  </div>
<div class="mdl-button mdl-button--icon">
  <i class="material-icons">search</i>
</div>
</form>`);
    }

    buildHtml(dto: SearchPanelTemplateDto) {
        return tardigradeEngine.buildHtml("SearchPanel", dto);
    }

    buildElement(dto: SearchPanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): SearchPanelTemplateElement {
        return {
            _root(): HTMLDivElement {
                return <HTMLDivElement>rootElement;
            },

            input() {
                return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "SearchPanel", { "input": 0 });
            }
        };
    }
}

export var searchPanelTemplate = new SearchPanelTemplate();