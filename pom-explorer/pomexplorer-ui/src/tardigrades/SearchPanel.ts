"use strict";

import { tardigradeEngine } from "../../node_modules/tardigrade/target/engine/engine";
import { ElementNode, TemplateNode, TextNode, Cardinal, PointInfo } from "../../node_modules/tardigrade/target/engine/model";
import { tardigradeParser } from "../../node_modules/tardigrade/target/engine/parser";
import { createElement, domChain, indexOf } from "../../node_modules/tardigrade/target/engine/runtime";



export interface SearchPanelDto {
    _root?: string;
    input?: any;
"@input"?: any;

}

export class SearchPanel {
    private static loaded = false;

    static ensureLoaded() {
        if(SearchPanel.loaded)
            return;
        SearchPanel.loaded = true;

        

        tardigradeEngine.addTemplate("SearchPanel", new ElementNode(null, <Cardinal>0, [""], "div", {}, [new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-textfield mdl-js-textfield mdl-textfield--floating-label"}, [new ElementNode("input", <Cardinal>0, [""], "input", {"class": "mdl-textfield__input", "type": "text", "id": "searchBox"}, []), new ElementNode(null, <Cardinal>0, [""], "label", {"class": "mdl-textfield__label", "for": "searchBox"}, [new TextNode("Project search...")])]), new ElementNode(null, <Cardinal>0, [""], "div", {"class": "mdl-button mdl-button--icon"}, [new ElementNode(null, <Cardinal>0, [""], "i", {"class": "material-icons"}, [new TextNode("search")])])]));
    }

    static html(dto: SearchPanelDto): string {
        SearchPanel.ensureLoaded();

        return tardigradeEngine.buildHtml("SearchPanel", dto);
    }

    static element(dto:SearchPanelDto): HTMLElement {
        return createElement(SearchPanel.html(dto));
    }

    static create(dto:SearchPanelDto): SearchPanel {
        let element = SearchPanel.element(dto);
        return new SearchPanel(element);
    }

    static of(element: HTMLElement): SearchPanel {
        return new SearchPanel(element);
    }

    constructor(private rootElement: HTMLElement) {}

    rootHtmlElement(): HTMLElement { return this.rootElement; }

    setUserData(data:any): any {
        let old = (this.rootElement as any)._tardigradeUserData || undefined;
        (this.rootElement as any)._tardigradeUserData = data;
        return old;
    }

    getUserData():any {
        return (this.rootElement as any)._tardigradeUserData || undefined;
    }

    input(): HTMLInputElement {
return <HTMLInputElement>tardigradeEngine.getPoint(this.rootElement, "SearchPanel", { "input": 0 });
}
inputHit(hitTest:HTMLElement): boolean {
                        let location = tardigradeEngine.getLocation(this.rootElement, "SearchPanel", hitTest);
                        return (location != null && ("input" in location));
                        }
}