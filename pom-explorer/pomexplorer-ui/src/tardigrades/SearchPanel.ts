"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface SearchPanelTemplateDto {
    _root?: string;
    input?: any;
"@input"?: any;

}

export interface SearchPanelTemplateElement {
    _root(): HTMLElement;
    input(): HTMLElement;

}

class SearchPanelTemplate {
    ensureLoaded() {
    }
    
    constructor() {
        
        
        tardigradeEngine.addTemplate("SearchPanel", tardigradeParser.parseTemplate(`<html>
<body>
<div>
    <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
        <input x-id="input" class="mdl-textfield__input" type="text" id="searchBox"/>
        <label class="mdl-textfield__label" for="searchBox">Project search...</label>
    </div>
    <div class="mdl-button mdl-button--icon">
    <i class="material-icons">search</i>
    </div>
</div>
</body>
</html>`));
    }

    buildHtml(dto: SearchPanelTemplateDto) {
        return tardigradeEngine.buildHtml("SearchPanel", dto);
    }

    buildElement(dto: SearchPanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): SearchPanelTemplateElement {
        let domlet = {
            _root() { return rootElement; },
            
            input(): HTMLElement{
return tardigradeEngine.getPoint(rootElement, "SearchPanel", { "input": 0 });
},

        };
        
        return domlet;
    }
}

export var searchPanelTemplate = new SearchPanelTemplate();
