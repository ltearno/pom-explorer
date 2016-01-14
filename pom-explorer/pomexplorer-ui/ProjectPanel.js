(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Card", "./Utils", "./node_modules/tardigrade/target/engine/runtime", "./node_modules/tardigrade/target/engine/engine"], factory);
    }
})(function (require, exports) {
    var Card_1 = require("./Card");
    var Utils_1 = require("./Utils");
    var runtime_1 = require("./node_modules/tardigrade/target/engine/runtime");
    var engine_1 = require("./node_modules/tardigrade/target/engine/engine");
    class SearchPanelTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("SearchPanel", `
<form action="#">
  <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
    <input x-id="input" class="mdl-textfield__input" type="text" id="searchBox">
    <label class="mdl-textfield__label" for="searchBox">Project search...</label>
  </div>
<div class="mdl-button mdl-button--icon">
  <i class="material-icons">search</i>
</div>
</form>`);
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("SearchPanel", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        of(rootElement) {
            return {
                _root() {
                    return rootElement;
                },
                input() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "SearchPanel", { "input": 0 });
                }
            };
        }
    }
    exports.searchPanelTemplate = new SearchPanelTemplate();
    class ProjectPanelTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("ProjectPanel", `
<div>
    <SearchPanel>
        <input x-id="searchInput"/>
    </SearchPanel>
    <div x-id="projectList" class='projects-list'></div>
</div>
`);
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml("ProjectPanel", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        of(rootElement) {
            return {
                _root() {
                    return rootElement;
                },
                searchInput() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "searchInput": 0 });
                },
                projectList() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "ProjectPanel", { "projectList": 0 });
                }
            };
        }
    }
    exports.projectPanelTemplate = new ProjectPanelTemplate();
    class ProjectPanel {
        constructor(service) {
            this.domlet = exports.projectPanelTemplate.of(exports.projectPanelTemplate.buildElement({}));
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
                this.service.sendRpc(value, (message) => {
                    this.domlet.projectList().innerHTML = "";
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