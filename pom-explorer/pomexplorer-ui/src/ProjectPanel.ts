"use strict";

import { cardTemplate } from "./tardigrades/Card";
import { baseCardTemplate } from "./tardigrades/BaseCard";
import { changeGavCardTemplate } from "./tardigrades/ChangeGavCard";
import { initMaterialElement, rx } from "./Utils";
import { Service, Status, Message, ServiceCallback } from "./Service";
import { createElement, domChain, indexOf } from "../node_modules/tardigrade/target/engine/runtime";
import { IWorkPanel } from "./IWorkPanel";
import { projectPanelTemplate, ProjectPanelTemplateElement } from "./tardigrades/ProjectPanel";
import { popupTemplate, PopupTemplateElement } from "./tardigrades/Popup";


export class ProjectPanel implements IWorkPanel {
    private domlet: ProjectPanelTemplateElement;

    private service: Service;

    constructor(service: Service) {
        this.domlet = projectPanelTemplate.of(projectPanelTemplate.buildElement({}));
        initMaterialElement(this.domlet._root());

        this.service = service;

        this.domlet.projectList().addEventListener("click", event => {
            let cardIndex = this.domlet.cardsIndex(event.target as HTMLElement);
            if (cardIndex < 0)
                return;

            let card = this.domlet.cardsDomlet(cardIndex);
            var dc = domChain(card._root(), event.target as HTMLElement);

            // details button
            if (Array.prototype.indexOf.call(dc, card.actionDetails()) >= 0) {
                if (card.details().style.display === "none")
                    card.details().style.display = null;
                else
                    card.details().style.display = "none";
            }
        });

        this.domlet.projectList().addEventListener("dblclick", event => {
            let cardIndex = this.domlet.cardsIndex(event.target as HTMLElement);
            if (cardIndex < 0)
                return;

            let card = this.domlet.cardsDomlet(cardIndex);
            var dc = domChain(card._root(), event.target as HTMLElement);

            if (Array.prototype.indexOf.call(dc, card.gav()) >= 0) {
                let popup = popupTemplate.createElement({
                    content: changeGavCardTemplate.buildHtml({
                        groupId: "yo",
                        artifactId: "yi",
                        version: "yyy",
                        "@groupIdInput": { "value": "gid" },
                        "@versionInput": { "value": "v" },
                        "@artifactIdInput": { "value": "aid" }
                    })
                });

                initMaterialElement(popup.content());

                document.getElementsByTagName('body')[0].appendChild(popup._root());
            }
        });

        rx.Observable.fromEvent(this.domlet.searchInput(), "input")
            .pluck("target", "value")
            .debounce(100)
            .distinctUntilChanged()
            .subscribe(value => {
                this.domlet.projectList().innerHTML = `<div class="mdl-progress mdl-js-progress mdl-progress__indeterminate"></div>`;
                initMaterialElement(<HTMLElement>this.domlet.projectList().children[0]);

                this.service.sendRpc(value, (message) => {
                    var list: Project[] = JSON.parse(message.payload);

                    var htmlString = "";

                    for (var pi in list) {
                        var project = list[pi];

                        let parts = project.gav.split(":");
                        let groupId = parts[0];
                        let artifactId = parts[1];
                        let version = parts[2];

                        var content = "";
                        if (project.buildable)
                            content += "<span class='badge'>buildable</span>";
                        content += `<span class='packaging'>${project.packaging}</span>`;
                        if (project.description)
                            content += project.description + "<br/><br/>";
                        if (project.parentChain && project.parentChain.length > 0)
                            content += `<i>parent${project.parentChain.length > 1 ? "s" : ""}</i><br/>${project.parentChain.join("<br/>")}<br/><br/>`;
                        if (project.file)
                            content += `<i>file</i> ${project.file}<br/><br/>`;
                        if (project.properties) {
                            var a = true;
                            for (var name in project.properties) {
                                if (a) {
                                    a = false;
                                    content += "<i>properties</i><br/>";
                                }
                                content += `${name}: <b>${project.properties[name]}</b><br/>`;
                            }
                            if (!a)
                                content += "<br/>";
                        }
                        if (project.references && project.references.length > 0) {
                            content += "<i>referenced by</i><br/>";
                            for (var ii = 0; ii < project.references.length; ii++) {
                                var ref = project.references[ii];
                                content += `${ref.gav} as ${ref.dependencyType}<br/>`;
                            }
                            content += "<br/>";
                        }

                        var details = "";
                        if (project.dependencyManagement) {
                            details += project.dependencyManagement;
                            details += "<br/>";
                        }
                        if (project.dependencies) {
                            details += project.dependencies;
                            details += "<br/>";
                        }
                        if (project.pluginManagement) {
                            details += project.pluginManagement;
                            details += "<br/>";
                        }
                        if (project.plugins) {
                            details += project.plugins;
                            details += "<br/>";
                        }

                        htmlString += cardTemplate.buildHtml({
                            gav: { groupId: groupId, artifactId: artifactId, version: version },
                            content: content,
                            details: details
                        });
                    }

                    this.domlet.projectList().innerHTML = htmlString;
                    initMaterialElement(this.domlet.projectList());
                });
            });
    }

    focus(): void {
        this.domlet.searchInput().focus();
    }

    element() {
        return this.domlet._root();
    }
}
