"use strict";

import { initMaterialElement } from "./Utils";

import { tardigradeEngine } from "../node_modules/tardigrade/target/engine/engine";
import { createElement, domChain, indexOf } from "../node_modules/tardigrade/target/engine/runtime";

interface CardTemplateDto {
    _root?: string;
    title?: string;
    content?: string;
    details?: string;
    actions?: string;
    actionDetails?: string;
    actionBuild?: string;
}

interface CardTemplateElement {
    _root(): HTMLDivElement;
    title(): HTMLElement;
    content(): HTMLDivElement;
    details(): HTMLDivElement;
    actions(): HTMLDivElement;
    actionDetails(): HTMLDivElement;
    actionBuild(): HTMLDivElement;
}

class CardTemplate {
    constructor() {
        tardigradeEngine.addTemplate("Card", `
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
`);
    }

    buildHtml(dto: CardTemplateDto) {
        return tardigradeEngine.buildHtml("Card", dto);
    }

    buildElement(dto: CardTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): CardTemplateElement {
        return {
            _root(): HTMLDivElement {
                return <HTMLDivElement>rootElement;
            },
            title(): HTMLElement {
                return tardigradeEngine.getPoint(rootElement, "Card", { "title": 0 });
            },
            content(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "content": 0 });
            },
            details(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "details": 0 });
            },
            actions(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "actions": 0 });
            },
            actionDetails(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionDetails": 0 });
            },
            actionBuild(): HTMLDivElement {
                return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionBuild": 0 });
            }
        };
    }
}

export var cardTemplate = new CardTemplate();
