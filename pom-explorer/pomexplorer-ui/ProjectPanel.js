(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./tardigrades/Card", "./Utils", "./node_modules/tardigrade/target/engine/runtime", "./tardigrades/ProjectPanel"], factory);
    }
})(function (require, exports) {
    "use strict";
    var Card_1 = require("./tardigrades/Card");
    var Utils_1 = require("./Utils");
    var runtime_1 = require("./node_modules/tardigrade/target/engine/runtime");
    var ProjectPanel_1 = require("./tardigrades/ProjectPanel");
    class ProjectPanel {
        constructor(service) {
            this.domlet = ProjectPanel_1.projectPanelTemplate.of(ProjectPanel_1.projectPanelTemplate.buildElement({}));
            Utils_1.initMaterialElement(this.domlet._root());
            this.service = service;
            this.domlet.projectList().addEventListener("click", event => {
                var dc = runtime_1.domChain(this.domlet.projectList(), event.target);
                var card = Card_1.cardTemplate.of(dc[1]);
                var cardDetailsButton = card.actionDetails();
                if (Array.prototype.indexOf.call(dc, cardDetailsButton) >= 0) {
                    if (card.details().style.display === "none")
                        card.details().style.display = null;
                    else
                        card.details().style.display = "none";
                }
            });
            Utils_1.rx.Observable.fromEvent(this.domlet.searchInput(), "input")
                .pluck("target", "value")
                .debounce(100)
                .distinctUntilChanged()
                .subscribe(value => {
                this.domlet.projectList().innerHTML = `<div class="mdl-progress mdl-js-progress mdl-progress__indeterminate"></div>`;
                Utils_1.initMaterialElement(this.domlet.projectList().children[0]);
                this.service.sendRpc(value, (message) => {
                    var list = JSON.parse(message.payload);
                    var htmlString = "";
                    for (var pi in list) {
                        var project = list[pi];
                        var title = "";
                        title += project.gav.split(":").join("<br/>");
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
                        htmlString += Card_1.cardTemplate.buildHtml({
                            title: title,
                            content: content,
                            details: details
                        });
                    }
                    this.domlet.projectList().innerHTML = htmlString;
                    Utils_1.initMaterialElement(this.domlet.projectList());
                });
            });
        }
        focus() {
            this.domlet.searchInput().focus();
        }
        element() {
            return this.domlet._root();
        }
    }
    exports.ProjectPanel = ProjectPanel;
});
//# sourceMappingURL=ProjectPanel.js.map