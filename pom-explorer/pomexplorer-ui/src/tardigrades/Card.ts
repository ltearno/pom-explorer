"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface CardTemplateDto {
    _root?: string;
    title?: any;
"@title"?: any;
content?: any;
"@content"?: any;
details?: any;
"@details"?: any;
actions?: any;
"@actions"?: any;
actionDetails?: any;
"@actionDetails"?: any;
actionBuild?: any;
"@actionBuild"?: any;

}

export interface CardTemplateElement {
    _root(): HTMLElement;
    title(): HTMLElement;
content(): HTMLDivElement;
details(): HTMLDivElement;
actions(): HTMLDivElement;
actionDetails(): HTMLElement;
actionBuild(): HTMLElement;
}

class CardTemplate {
    ensureLoaded() {
    }

    constructor() {
        

        tardigradeEngine.addTemplate("Card", tardigradeParser.parseTemplate(`<html>
<body>
<div class="project-card mdl-card mdl-shadow--2dp">
  <div class="mdl-card__title mdl-card--expand">
    <h2 x-id="title" class="mdl-card__title-text"/>
  </div>
  <div x-id="content" class="mdl-card__supporting-text"/>
  <div x-id="details" class="mdl-card__supporting-text" style="display:none;"/>
  <div x-id="actions" class="mdl-card__actions mdl-card--border">
    <a x-id="actionDetails" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Details</a>
    <a x-id="actionBuild" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Build</a>
  </div>
</div>
</body>
</html>`));
    }

    buildHtml(dto: CardTemplateDto) {
        return tardigradeEngine.buildHtml("Card", dto);
    }

    buildElement(dto: CardTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): CardTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            title(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "title": 0 });
},
content(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "content": 0 });
},
details(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "details": 0 });
},
actions(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "actions": 0 });
},
actionDetails(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionDetails": 0 });
},
actionBuild(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionBuild": 0 });
}
        };

        return domlet;
    }
}

export var cardTemplate = new CardTemplate();