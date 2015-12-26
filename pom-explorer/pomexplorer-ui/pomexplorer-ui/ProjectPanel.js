var ProjectPanelDomlet = new MaterialDomlet("\n<div>\n    <div></div>\n    <div class='projects-list'></div>\n</div>\n", {
    'search-place': [0],
    'project-list': [1]
});
var ProjectPanel = (function () {
    function ProjectPanel(service) {
        var _this = this;
        this.element = ProjectPanelDomlet.htmlElement();
        this.service = service;
        this.search = SearchPanelDomlet.htmlElement();
        ProjectPanelDomlet.point("search-place", this.element).appendChild(this.search);
        Rx.Observable.fromEvent(SearchPanelDomlet.input(this.search), "input")
            .pluck("target", "value")
            .debounce(100)
            .distinctUntilChanged()
            .subscribe(function (value) {
            _this.service.sendRpc(value, function (message) {
                _this.projectList().innerHTML = "";
                var list = JSON.parse(message.payload);
                var htmlString = "";
                for (var pi in list) {
                    var project = list[pi];
                    var title = "";
                    title += project.gav.split(":").join("<br/>");
                    var content = "";
                    if (project.buildable)
                        content += "<span class='badge'>buildable</span>";
                    content += "<span class='packaging'>" + project.packaging + "</span>";
                    if (project.description)
                        content += project.description + "<br/><br/>";
                    if (project.parentChain && project.parentChain.length > 0)
                        content += "<i>parent" + (project.parentChain.length > 1 ? "s" : "") + ":</i><br/>" + project.parentChain.join("<br/>") + "<br/><br/>";
                    if (project.file)
                        content += "<i>file:</i> " + project.file + "<br/><br/>";
                    if (project.properties) {
                        var a = true;
                        for (var name in project.properties) {
                            if (a) {
                                a = false;
                                content += "<i>properties:</i><br/>";
                            }
                            content += name + ": <b>" + project.properties[name] + "</b><br/>";
                        }
                        if (!a)
                            content += "<br/>";
                    }
                    if (project.references && project.references.length > 0) {
                        content += "<i>referenced by:</i><br/>";
                        for (var ii = 0; ii < project.references.length; ii++) {
                            var ref = project.references[ii];
                            content += ref.gav + " as " + ref.dependencyType + "<br/>";
                        }
                    }
                    htmlString += CardDomlet.html({
                        title: title,
                        content: content
                    });
                }
                _this.projectList().innerHTML = htmlString;
                CardDomlet.initMaterialElement(_this.projectList());
            });
        });
    }
    ProjectPanel.prototype.searchInput = function () {
        var search = ProjectPanelDomlet.point("search-place", this.element);
        return SearchPanelDomlet.input(search);
    };
    ProjectPanel.prototype.projectList = function () {
        return ProjectPanelDomlet.point("project-list", this.element);
    };
    return ProjectPanel;
})();
//# sourceMappingURL=ProjectPanel.js.map