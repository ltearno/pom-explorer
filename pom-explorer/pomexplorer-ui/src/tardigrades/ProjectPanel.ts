"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { searchPanelTemplate, SearchPanelTemplateElement } from "./SearchPanel";

export interface ProjectPanelTemplateDto {
    _root?: string;
    searchInput?: any;
"@searchInput"?: any;
projectList?: any;
"@projectList"?: any;

}

export interface ProjectPanelTemplateElement {
    _root(): HTMLElement;
    searchInput(): HTMLElement;
projectList(): HTMLDivElement;
}

class ProjectPanelTemplate {
    ensureLoaded() {
    }

    constructor() {
        searchPanelTemplate.ensureLoaded();

        tardigradeEngine.addTemplate("ProjectPanel", new ElementNode(null, <Cardinal>0, [""], "div", {}, [new TemplateNode(null, <Cardinal>0, [""], "SearchPanel", {}, {"input": new PointInfo("searchInput", {}, [])}), new ElementNode("projectList", <Cardinal>0, [""], "div", {"class": "projects-list"}, [])]));
    }

    buildHtml(dto: ProjectPanelTemplateDto) {
        return tardigradeEngine.buildHtml("ProjectPanel", dto);
    }

    buildElement(dto: ProjectPanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    createElement(dto: ProjectPanelTemplateDto): ProjectPanelTemplateElement {
        return this.of(this.buildElement(dto));
    }

    of(rootElement: HTMLElement): ProjectPanelTemplateElement {
        let domlet = {
            _root() { return rootElement; },

            searchInput(): HTMLElement{
return <HTMLElement>tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "searchInput": 0 });
},
projectList(): HTMLDivElement{
return <HTMLDivElement>tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "projectList": 0 });
}
        };

        return domlet;
    }
}

export var projectPanelTemplate = new ProjectPanelTemplate();