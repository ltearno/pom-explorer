"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";

import { searchPanelTemplate } from "./SearchPanel";

export interface ProjectPanelTemplateDto {

}

export interface ProjectPanelTemplateElement {
    _root(): HTMLElement;
    searchInput(): HTMLInputElement;
    projectList(): HTMLElement;
}

class ProjectPanelTemplate {
    constructor() {
        searchPanelTemplate.ensureLoaded();
        
        tardigradeEngine.addTemplate("ProjectPanel", `
<div>
    <SearchPanel>
        <input x-id="searchInput"/>
    </SearchPanel>
    <div x-id="projectList" class='projects-list'></div>
</div>
`);
    }

    buildHtml(dto: ProjectPanelTemplateDto) {
        return tardigradeEngine.buildHtml("ProjectPanel", dto);
    }

    buildElement(dto: ProjectPanelTemplateDto) {
        return createElement(this.buildHtml(dto));
    }

    of(rootElement: HTMLElement): ProjectPanelTemplateElement {
        return {
            _root(): HTMLDivElement {
                return <HTMLDivElement>rootElement;
            },

            searchInput() {
                return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "searchInput": 0 });
            },

            projectList() {
                return <HTMLInputElement>tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "projectList": 0 });
            }
        };
    }
}

export var projectPanelTemplate = new ProjectPanelTemplate();