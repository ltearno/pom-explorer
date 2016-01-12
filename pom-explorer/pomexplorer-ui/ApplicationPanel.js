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
    class ApplicationTemplate {
        constructor() {
            this.id = "Application";
            engine_1.tardigradeEngine.addTemplate(this.id, `
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Pom Explorer</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="mdl-badge" data-badge="!">beta</span>
        </div>
    </header>
    <div x-id="Drawer" class="mdl-layout__drawer">
        <span class="mdl-layout-title">Pom Explorer</span>
        <nav x-id="Menu" class="mdl-navigation">
            <a x-id="MenuItems" x-cardinal="*" class="mdl-navigation__link" href="#"/>
        </nav>
    </div>
    <main x-id="Content" class="mdl-layout__content content-repositionning"/>
</div>
`);
        }
        buildHtml(dto) {
            return engine_1.tardigradeEngine.buildHtml(this.id, dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        content(rootElement) {
            return engine_1.tardigradeEngine.getPoint(rootElement, this.id, { "Content": 0 });
        }
        menu(rootElement) {
            return engine_1.tardigradeEngine.getPoint(rootElement, this.id, { "Menu": 0 });
        }
        menuItems(rootElement, menuItemsIndex) {
            return engine_1.tardigradeEngine.getPoint(rootElement, this.id, { "Menu": 0, "MenuItems": menuItemsIndex });
        }
        drawer(rootElement) {
            return engine_1.tardigradeEngine.getPoint(rootElement, this.id, { "Drawer": 0 });
        }
        menuItemsIndex(rootElement, hitTest) {
            let location = this.getLocation(rootElement, hitTest);
            if (location != null && ("MenuItems" in location))
                return location["MenuItems"];
            return -1;
        }
        addMenuItem(rootElement, dto) {
            // grab the parent node
            // TODO this may be not doable if the parent does not have an id...
            // TODO think about a better solution...
            let menu = applicationTemplate.menu(rootElement);
            let menuItem = this.buildNodeHtml("MenuItems", dto);
            menu.appendChild(runtime_1.createElement(menuItem));
        }
        getLocation(rootElement, hitTest) {
            return engine_1.tardigradeEngine.getLocation(rootElement, this.id, hitTest);
        }
        buildNodeHtml(nodeId, dto) {
            return engine_1.tardigradeEngine.buildNodeHtml(this.id, nodeId, dto);
        }
    }
    var applicationTemplate = new ApplicationTemplate();
    class ApplicationPanel {
        constructor() {
            this.element = applicationTemplate.buildElement({});
            Utils_1.initMaterialElement(this.element);
            applicationTemplate.menu(this.element).innerHTML = "";
        }
        addMenuHandler(handler) {
            var menu = applicationTemplate.menu(this.element);
            menu.addEventListener("click", (e) => {
                var target = e.target;
                let menuItemsIndex = applicationTemplate.menuItemsIndex(this.element, target);
                if (menuItemsIndex >= 0) {
                    let menuItem = applicationTemplate.menuItems(this.element, menuItemsIndex);
                    handler(menuItemsIndex, menuItem.innerText, e);
                    this.hideDrawer();
                }
            });
        }
        addMenuItem(name) {
            applicationTemplate.addMenuItem(this.element, { _root: name });
        }
        main() {
            return this.element;
        }
        setContent(contentElement) {
            let content = applicationTemplate.content(this.element);
            content.innerHTML = "";
            if (contentElement != null)
                content.appendChild(contentElement);
        }
        hideDrawer() {
            // fix : the obfuscator is still visible if only remove is-visible from the drawer
            document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
            applicationTemplate.drawer(this.element).classList.remove("is-visible");
        }
    }
    exports.ApplicationPanel = ApplicationPanel;
});
//# sourceMappingURL=ApplicationPanel.js.map