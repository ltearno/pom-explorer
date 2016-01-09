(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Utils", "./node_modules/tardigrade/target/engine/engine", "./node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    var Utils_1 = require("./Utils");
    var engine_1 = require("./node_modules/tardigrade/target/engine/engine");
    var runtime_1 = require("./node_modules/tardigrade/target/engine/runtime");
    engine_1.TardigradeEngine.addTemplate("Application", `
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">OHOH Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div x-id="Drawer" class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav x-id="Menu" class="mdl-navigation">
            <div x-id="MenuItems" x-cardinal="*"/>
        </nav>
    </div>
    <main x-id="Content" class="mdl-layout__content content-repositionning">
    </main>
</div>
`);
    function initMaterialElement(e) {
        if (e == null)
            return;
        var upgrade = false;
        for (var i = 0; i < e.classList.length; i++)
            if (e.classList[i].indexOf("mdl-") >= 0) {
                upgrade = true;
                break;
            }
        if (upgrade)
            componentHandler.upgradeElement(e);
        for (var c in e.children) {
            if (e.children[c] instanceof HTMLElement)
                initMaterialElement(e.children[c]);
        }
    }
    function getComingChild(p, element, domletElement) {
        var directChild = element;
        while (directChild != null && directChild.parentElement !== p) {
            if (directChild === domletElement)
                return null;
            directChild = directChild.parentElement;
        }
        return directChild;
    }
    class ApplicationPanel {
        constructor() {
            this.element = runtime_1.createElement(engine_1.TardigradeEngine.buildHtml("Application", {}));
            initMaterialElement(this.element);
        }
        addMenuHandler(handler) {
            var menu = engine_1.TardigradeEngine.getPoint(this.element, "Application", { "Menu": 0 });
            menu.addEventListener("click", (e) => {
                var target = e.target;
                var location = engine_1.TardigradeEngine.getLocation(this.element, "Application", target);
                if (location != null && ("MenuItems" in location)) {
                    let index = location["MenuItems"];
                    let menuItem = engine_1.TardigradeEngine.getPoint(this.element, "Application", location);
                    handler(index, menuItem, e);
                    this.hideDrawer();
                }
            });
        }
        addMenuItem(name) {
            var menu = engine_1.TardigradeEngine.getPoint(this.element, "Application", { "Menu": 0 });
            menu.appendChild(Utils_1.buildHtmlElement(`<a class="mdl-navigation__link" href="#">${name}</a>`));
        }
        main() {
            return this.element;
        }
        content() {
            return engine_1.TardigradeEngine.getPoint(this.element, "Application", { "Content": 0 });
        }
        hideDrawer() {
            // fix : the obfuscator is still visible if only remove is-visible from the drawer
            document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
            engine_1.TardigradeEngine.getPoint(this.element, "Application", { "Drawer": 0 }).classList.remove("is-visible");
        }
    }
    exports.ApplicationPanel = ApplicationPanel;
});
//# sourceMappingURL=ApplicationPanel.js.map