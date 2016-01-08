(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./MaterialDomlet", "./Utils"], factory);
    }
})(function (require, exports) {
    var MaterialDomlet_1 = require("./MaterialDomlet");
    var Utils_1 = require("./Utils");
    var ApplicationPanelDomlet = new MaterialDomlet_1.MaterialDomlet(`
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav class="mdl-navigation">
        </nav>
    </div>
    <main class="mdl-layout__content content-repositionning">
    </main>
</div>
`, {
        'main': [],
        'content': [2],
        'menu': [1, 1],
        'drawer': [1]
    });
    class ApplicationPanel {
        constructor() {
            this.element = ApplicationPanelDomlet.htmlElement();
        }
        addMenuHandler(handler) {
            var menu = ApplicationPanelDomlet.point("menu", this.element);
            menu.addEventListener("click", (e) => {
                var target = e.target;
                var comingMenuItem = ApplicationPanelDomlet.getComingChild(menu, target, this.element);
                var index = Utils_1.indexOf(menu, comingMenuItem);
                handler(index, comingMenuItem, e);
                this.hideDrawer();
            });
        }
        addMenuItem(name) {
            var menu = ApplicationPanelDomlet.point("menu", this.element);
            menu.appendChild(Utils_1.buildHtmlElement(`<a class="mdl-navigation__link" href="#">${name}</a>`));
        }
        main() {
            return ApplicationPanelDomlet.point("main", this.element);
        }
        content() {
            return ApplicationPanelDomlet.point("content", this.element);
        }
        hideDrawer() {
            // fix : the obfuscator is still visible if only remove is-visible from the drawer
            document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
            ApplicationPanelDomlet.point("drawer", this.element).classList.remove("is-visible");
        }
    }
    exports.ApplicationPanel = ApplicationPanel;
});
//# sourceMappingURL=ApplicationPanel.js.map