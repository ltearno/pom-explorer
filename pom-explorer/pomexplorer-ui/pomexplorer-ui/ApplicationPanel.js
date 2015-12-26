var ApplicationPanelDomlet = new MaterialDomlet("\n<div class=\"mdl-layout mdl-js-layout mdl-layout--fixed-header\">\n    <header class=\"mdl-layout__header\">\n        <div class=\"mdl-layout__header-row\">\n            <span class=\"mdl-layout-title\">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"mdl-badge\" data-badge=\"!\">beta</span>\n        </div>\n    </header>\n    <div class=\"mdl-layout__drawer\">\n        <span class=\"mdl-layout-title\">Pom Explorer</span>\n        <nav class=\"mdl-navigation\">\n        </nav>\n    </div>\n    <main class=\"mdl-layout__content content-repositionning\">\n    </main>\n</div>\n", {
    'main': [],
    'content': [2],
    'menu': [1, 1],
    'drawer': [1]
});
var ApplicationPanel = (function () {
    function ApplicationPanel() {
        this.element = ApplicationPanelDomlet.htmlElement();
    }
    ApplicationPanel.prototype.addMenuHandler = function (handler) {
        var _this = this;
        var menu = ApplicationPanelDomlet.point("menu", this.element);
        menu.addEventListener("click", function (e) {
            var target = e.target;
            var comingMenuItem = ApplicationPanelDomlet.getComingChild(menu, target, _this.element);
            var index = indexOf(menu, comingMenuItem);
            handler(index, comingMenuItem, e);
            _this.hideDrawer();
        });
    };
    ApplicationPanel.prototype.addMenuItem = function (name) {
        var menu = ApplicationPanelDomlet.point("menu", this.element);
        menu.appendChild(buildHtmlElement("<a class=\"mdl-navigation__link\" href=\"#\">" + name + "</a>"));
    };
    ApplicationPanel.prototype.main = function () {
        return ApplicationPanelDomlet.point("main", this.element);
    };
    ApplicationPanel.prototype.content = function () {
        return ApplicationPanelDomlet.point("content", this.element);
    };
    ApplicationPanel.prototype.hideDrawer = function () {
        // fix : the obfuscator is still visible if only remove is-visible from the drawer
        document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
        ApplicationPanelDomlet.point("drawer", this.element).classList.remove("is-visible");
    };
    return ApplicationPanel;
})();
//# sourceMappingURL=ApplicationPanel.js.map