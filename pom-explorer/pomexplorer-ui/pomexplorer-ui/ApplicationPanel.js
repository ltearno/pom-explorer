var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var ApplicationPanel = (function (_super) {
    __extends(ApplicationPanel, _super);
    function ApplicationPanel() {
        _super.call(this, "\n<div class=\"mdl-layout mdl-js-layout mdl-layout--fixed-header\">\n    <header class=\"mdl-layout__header\">\n        <div class=\"mdl-layout__header-row\">\n            <span class=\"mdl-layout-title\">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"mdl-badge\" data-badge=\"!\">beta</span>\n        </div>\n    </header>\n    <div class=\"mdl-layout__drawer\">\n        <span class=\"mdl-layout-title\">Pom Explorer</span>\n        <nav class=\"mdl-navigation\">\n        </nav>\n    </div>\n    <main class=\"mdl-layout__content content-repositionning\">\n    </main>\n</div>\n", {
            'main': [],
            'content': [2],
            'menu': [1, 1],
            'drawer': [1]
        });
    }
    ApplicationPanel.prototype.addMenuHandler = function (handler) {
        var _this = this;
        var menu = this.point("menu");
        menu.addEventListener("click", function (e) {
            var target = e.target;
            var comingMenuItem = _this.getComingChild(menu, target);
            var index = indexOf(menu, comingMenuItem);
            handler(index, comingMenuItem, e);
            //e.preventDefault();
            //e.stopPropagation();
            console.log("click menu index: " + index);
            _this.hideDrawer();
        });
    };
    ApplicationPanel.prototype.addMenuItem = function (name) {
        var menu = this.point("menu");
        menu.appendChild(buildHtml("<a class=\"mdl-navigation__link\" href=\"#\">" + name + "</a>"));
    };
    ApplicationPanel.prototype.main = function () {
        return this.point("main");
    };
    ApplicationPanel.prototype.content = function () {
        return this.point("content");
    };
    ApplicationPanel.prototype.hideDrawer = function () {
        // fix : the obfuscator is still visible if only remove is-visible from the drawer
        document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
        this.point("drawer").classList.remove("is-visible");
    };
    return ApplicationPanel;
})(MaterialDomlet);
//# sourceMappingURL=ApplicationPanel.js.map