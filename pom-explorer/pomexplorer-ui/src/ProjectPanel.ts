"use strict";

import { Card } from "./tardigrades/Card";
import { BaseCard } from "./tardigrades/BaseCard";
import { ChangeGavCard } from "./tardigrades/ChangeGavCard";
import { initMaterialElement, rx } from "./Utils";
import { Service, Status, Message, ServiceCallback } from "./Service";
import { IWorkPanel } from "./IWorkPanel";
import { ProjectPanel as ProjectPanelTemplate } from "./tardigrades/ProjectPanel";
import { Popup } from "./tardigrades/Popup";

export class ProjectPanel implements IWorkPanel {
    private domlet: ProjectPanelTemplate;

    constructor(private service: Service) {
        this.domlet = ProjectPanelTemplate.create({});
        initMaterialElement(this.domlet.rootHtmlElement());

        this.domlet.projectList().addEventListener("click", event => {
            this.forDetailsToggle(event.target as HTMLElement);
            this.forChangeGav(event.target as HTMLElement);
        });
        this.domlet.projectList().addEventListener("dblclick", event => this.forChangeGav(event.target as HTMLElement));

        rx.Observable.fromEvent(this.domlet.searchInput(), "input")
            .pluck("target", "value")
            .debounce(300)
            .distinctUntilChanged()
            .subscribe(value => {
                this.domlet.projectList().innerHTML = `<div class="mdl-progress mdl-js-progress mdl-progress__indeterminate"></div>`;
                initMaterialElement(<HTMLElement>this.domlet.projectList().children[0]);

                let rpcCall = {
                    "service": "projects",
                    "method": "list",
                    "parameters": {
                        "query": value
                    }
                };

                this.service.sendRpc(rpcCall, (message) => {
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
                            content += `<i>file</i><br/>${project.file}<br/><br/>`;
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

                        htmlString += Card.html({
                            gav: { gavGroupId: groupId, gavArtifactId: artifactId, gavVersion: version },
                            content: content,
                            details: details
                        });
                    }

                    this.domlet.projectList().innerHTML = htmlString;
                    initMaterialElement(this.domlet.projectList());

                    let elements = this.domlet.projectList().children;
                    for (var pi in list) {
                        var project = list[pi];

                        Card.of(<HTMLElement>elements.item(pi)).setUserData(project);
                    }
                });
            });
    }

    focus(): void {
        this.domlet.searchInput().focus();
    }

    element() {
        return this.domlet.rootHtmlElement();
    }

    private forDetailsToggle(hitElement: HTMLElement) {
        let card = this.domlet.cardsHitDomlet(hitElement);
        if (card == null)
            return;

        if (card.actionDetailsHit(hitElement)) {
            if (card.details().style.display === "none")
                card.details().style.display = null;
            else
                card.details().style.display = "none";
        }
    }

    private forChangeGav(hitElement: HTMLElement) {
        let card = this.domlet.cardsHitDomlet(hitElement);
        if (card == null)
            return;

        let project = card.getUserData();
        let parts = project.gav.split(":");
        let groupId = parts[0];
        let artifactId = parts[1];
        let version = parts[2];

        if (card.editHit(hitElement) || card.gavHit(hitElement)) {
            let changeCard = ChangeGavCard.create({
                groupId: groupId,
                artifactId: artifactId,
                version: version,
                "@groupIdInput": { "value": groupId },
                "@artifactIdInput": { "value": artifactId },
                "@versionInput": { "value": version }
            });
            initMaterialElement(changeCard.rootHtmlElement());

            let popup = Popup.create({});
            popup.content().appendChild(changeCard.rootHtmlElement());

            document.getElementsByTagName('body')[0].appendChild(popup.rootHtmlElement());

            changeCard.rootHtmlElement().addEventListener("click", event => {

                let test = changeCard.groupIdInput();

                let hit = event.target as HTMLElement;
                if (changeCard.actionCancelHit(hit)) {
                    popup.rootHtmlElement().remove();
                }
                else if (changeCard.actionValidateHit(hit)) {
                    let rpcCall = {
                        "service": "gav",
                        "method": "change",
                        "parameters": {
                            "oldGav": project.gav,
                            "newGav": `${changeCard.groupIdInput().value}:${changeCard.artifactIdInput().value}:${changeCard.artifactIdInput().value}`
                        }
                    };

                    popup.rootHtmlElement().style.opacity = "0.5";
                    this.service.sendRpc(rpcCall, (message) => {
                        var result = JSON.parse(message.payload);
                        alert(message.payload);
                        // TODO : call service and manage results...
                        // this.service.
                        popup.rootHtmlElement().remove();
                    });
                }
            });
        }
    }
}
