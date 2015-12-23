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
                for (var ii in list) {
                    card = new Card();
                    card.title().innerHTML = list[ii];
                    card.content().innerText = "Another fundamental part of creating programs in JavaScript for webpages and servers alike is working with textual data.";
                    _this.projectList().appendChild(card.element);
                }
            });
        });
        for (var i = 0; i < 0; i++) {
            card = new Card();
            card.title().innerHTML = "fr.lteconsulting<br/>accounting<br/>1.0-SNAPSHOT";
            card.content().innerText = "Another fundamental part of creating programs in JavaScript for webpages and servers alike is working with textual data.";
            this.projectList().appendChild(card.element);
        }
    }
    ProjectPanel.prototype.projectList = function () {
        return this.point("project-list");
    };
    return ProjectPanel;
})(MaterialDomlet);
//# sourceMappingURL=ProjectPanel.js.map