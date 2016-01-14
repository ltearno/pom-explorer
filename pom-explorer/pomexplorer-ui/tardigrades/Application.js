(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "../node_modules/tardigrade/target/engine/engine", "../node_modules/tardigrade/target/engine/runtime"], factory);
    }
})(function (require, exports) {
    "use strict";
    var engine_1 = require("../node_modules/tardigrade/target/engine/engine");
    var runtime_1 = require("../node_modules/tardigrade/target/engine/runtime");
    class ApplicationTemplate {
        constructor() {
            engine_1.tardigradeEngine.addTemplate("Application", `
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
            return engine_1.tardigradeEngine.buildHtml("Application", dto);
        }
        buildElement(dto) {
            return runtime_1.createElement(this.buildHtml(dto));
        }
        of(rootElement) {
            let me = {
                _root() { return rootElement; },
                content() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Application", { "Content": 0 });
                },
                menu() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Application", { "Menu": 0 });
                },
                menuItems(menuItemsIndex) {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Application", { "Menu": 0, "MenuItems": menuItemsIndex });
                },
                drawer() {
                    return engine_1.tardigradeEngine.getPoint(rootElement, "Application", { "Drawer": 0 });
                },
                menuItemsIndex(hitTest) {
                    let location = engine_1.tardigradeEngine.getLocation(rootElement, "Application", hitTest);
                    if (location != null && ("MenuItems" in location))
                        return location["MenuItems"];
                    return -1;
                },
                // TODO not sure
                addMenuItem(dto) {
                    let menuItem = engine_1.tardigradeEngine.buildNodeHtml("Application", "MenuItems", dto);
                    me.menu().appendChild(runtime_1.createElement(menuItem));
                }
            };
            return me;
        }
    }
    exports.applicationTemplate = new ApplicationTemplate();
});
//# sourceMappingURL=Application.js.map