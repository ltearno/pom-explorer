var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var ProjectPanel = (function (_super) {
    __extends(ProjectPanel, _super);
    function ProjectPanel(service) {
        var _this = this;
        _super.call(this, "\n<div>\n    <div></div>\n    <div class='projects-list'></div>\n</div>\n", {
            'search-place': [0],
            'project-list': [1]
        });
        this.service = service;
        this.search = new SearchPanel();
        this.point("search-place").appendChild(this.search.element);
        var card;
        this.search.input().addEventListener("input", function (e) {
            var value = e.target.value;
            _this.service.sendRpc(value, function (message) {
                _this.projectList().innerHTML = "";
                var list = JSON.parse(message.payload);
                for (var pi in list) {
                    var project = list[pi];
                    card = new Card();
                    var title = "";
                    title += project.gav.split(":").join("<br/>");
                    card.title().innerHTML = title;
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
                        console.log('rr ' + project.references);
                        for (var ii = 0; ii < project.references.length; ii++) {
                            var ref = project.references[ii];
                            content += ref.gav + " as " + ref.dependencyType + "<br/>";
                        }
                    }
                    card.content().innerHTML = content;
                    _this.projectList().appendChild(card.element);
                }
            });
        });
    }
    ProjectPanel.prototype.projectList = function () {
        return this.point("project-list");
    };
    return ProjectPanel;
})(MaterialDomlet);
//# sourceMappingURL=ProjectPanel.js.map