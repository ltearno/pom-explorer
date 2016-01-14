(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Utils", "./tardigrades/Application"], factory);
    }
})(function (require, exports) {
    "use strict";
    var Utils_1 = require("./Utils");
    var Application_1 = require("./tardigrades/Application");
    class ApplicationPanel {
        constructor() {
            this.template = Application_1.applicationTemplate.of(Application_1.applicationTemplate.buildElement({}));
            Utils_1.initMaterialElement(this.template._root());
            this.template.menu().innerHTML = "";
        }
        addMenuHandler(handler) {
            var menu = this.template.menu();
            menu.addEventListener("click", (e) => {
                var target = e.target;
                let menuItemsIndex = this.template.menuItemsIndex(target);
                if (menuItemsIndex >= 0) {
                    let menuItem = this.template.menuItems(menuItemsIndex);
                    handler(menuItemsIndex, menuItem.innerText, e);
                    this.hideDrawer();
                }
            });
        }
        addMenuItem(name) {
            this.template.addMenuItem({ _root: name });
        }
        main() {
            return this.template._root();
        }
        setContent(contentElement) {
            let content = this.template.content();
            content.innerHTML = "";
            if (contentElement != null)
                content.appendChild(contentElement);
        }
        hideDrawer() {
            // fix : the obfuscator is still visible if only remove is-visible from the drawer
            document.getElementsByClassName("mdl-layout__obfuscator")[0].classList.remove("is-visible");
            this.template.drawer().classList.remove("is-visible");
        }
    }
    exports.ApplicationPanel = ApplicationPanel;
});
//# sourceMappingURL=ApplicationPanel.js.map