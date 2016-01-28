"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { gavTemplate, GavTemplateElement } from "./Gav";

export interface CardTemplateDto {
    _root?: string;
    title?: any;
    "@title"?: any;
    gav?: any;
    "@gav"?: any;
    gavGroupId?: any;
    "@gavGroupId"?: any;
    gavArtifactId?: any;
    "@gavArtifactId"?: any;
    gavVersion?: any;
    "@gavVersion"?: any;
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
    gav(): HTMLElement;
    gavDomlet(): GavTemplateElement;
    gavGroupId(): HTMLElement;
    gavArtifactId(): HTMLElement;
    gavVersion(): HTMLElement;
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
        gavTemplate.ensureLoaded();

        tardigradeEngine.addTemplate("Card", new ElementNode(null, <Cardinal>0, [""], "div", { "class": "project-card mdl-card mdl-shadow--2dp" }, [new ElementNode(null, <Cardinal>0, [""], "div", { "class": "mdl-card__title mdl-card--expand" }, [new ElementNode("title", <Cardinal>0, [""], "h2", { "class": "mdl-card__title-text" }, []), new TemplateNode("gav", <Cardinal>0, ["export"], "Gav", { "class": "mdl-card__title-text" }, { "groupId": new PointInfo("gavGroupId", {}, []), "artifactId": new PointInfo("gavArtifactId", {}, []), "version": new PointInfo("gavVersion", {}, []) })]), new ElementNode("content", <Cardinal>0, [""], "div", { "class": "mdl-card__supporting-text" }, []), new ElementNode("details", <Cardinal>0, [""], "div", { "class": "mdl-card__supporting-text", "style": "display:none;" }, []), new ElementNode("actions", <Cardinal>0, [""], "div", { "class": "mdl-card__actions mdl-card--border" }, [new ElementNode("actionDetails", <Cardinal>0, [""], "a", { "class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" }, [new TextNode("Details")]), new ElementNode("actionBuild", <Cardinal>0, [""], "a", { "class": "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" }, [new TextNode("Build")])])]));
    }

    buildHtml(dto: CardTemplateDto) {
        return tardigradeEngine.buildHtml("Card", dto);
    }

    buildElement(dto: CardTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: CardTemplateDto): CardTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): CardTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            title(): HTMLElement {
                return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "title": 0 });
            },
            gav(): HTMLElement {
                return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gav": 0 });
            },
            gavDomlet(): GavTemplateElement {
                let element = domlet.gav();
                return gavTemplate.of(element);
            },
            gavGroupId(): HTMLElement {
                return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavGroupId": 0 });
            },
            gavArtifactId(): HTMLElement {
                return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavArtifactId": 0 });
            },
            gavVersion(): HTMLElement {
                return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "gavVersion": 0 });
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
            actionDetails(): HTMLElement {
                return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionDetails": 0 });
            },
            actionBuild(): HTMLElement {
                return <HTMLElement>tardigradeEngine.getPoint(rootElement, "Card", { "actionBuild": 0 });
            }
        };

        return domlet;
    }
}

export var cardTemplate = new CardTemplate();