(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet", "./Card", "./SearchPanel", "./Utils", "./node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    var Card_1 = require("./Card");
    var SearchPanel_1 = require("./SearchPanel");
    var Utils_1 = require("./Utils");
    var runtime_1 = require("./node_modules/tardigrade/target/engine/runtime");
    var ProjectPanelDomlet = new MaterialDomlet_1.MaterialDomlet(`
<div>
    <div></div>
    <div class='projects-list'></div>
</div>
`, {
        'search-place': [0],
        'project-list': [1]
    });
    class ProjectPanel {
        constructor(service) {
            this.element = ProjectPanelDomlet.htmlElement();
            this.service = service;
            var search = SearchPanel_1.SearchPanelDomlet.htmlElement();
            ProjectPanelDomlet.point("search-place", this.element).appendChild(search);
            this.projectList().addEventListener("click", event => {
                var dc = runtime_1.domChain(this.projectList(), event.target);
                var card = dc[1];
                var cardDetailsButton = Card_1.CardDomlet.actionsDetails(card);
                if (Array.prototype.indexOf.call(dc, cardDetailsButton) >= 0) {
                    if (Card_1.CardDomlet.details(card).style.display === "none")
                        Card_1.CardDomlet.details(card).style.display = null;
                    else
                        Card_1.CardDomlet.details(card).style.display = "none";
                }
            });
            Utils_1.rx.Observable.fromEvent(SearchPanel_1.SearchPanelDomlet.input(search), "input")
                .pluck("target", "value")
                .debounce(100)
                .distinctUntilChanged()
                .subscribe(value => {
                this.service.sendRpc(value, (message) => {
                    this.projectList().innerHTML = "";
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
                        htmlString += Card_1.CardDomlet.html({
                            title: title,
                            content: content,
                            details: details
                        });
                    }
                    this.projectList().innerHTML = htmlString;
                    Card_1.CardDomlet.initMaterialElement(this.projectList());
                });
            });
        }
        searchInput() {
            var search = ProjectPanelDomlet.point("search-place", this.element);
            return SearchPanel_1.SearchPanelDomlet.input(search);
        }
        projectList() {
            return ProjectPanelDomlet.point("project-list", this.element);
        }
    }
    exports.ProjectPanel = ProjectPanel;
});
//# sourceMappingURL=ProjectPanel.js.map