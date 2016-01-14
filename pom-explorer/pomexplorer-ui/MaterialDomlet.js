(function (factory) {
    if (typeof module === 'object' && typeof module.exports === 'object') {
        var v = factory(require, exports); if (v !== undefined) module.exports = v;
    }
    else if (typeof define === 'function' && define.amd) {
        define(["require", "exports", "./Domlet"], factory);
    }
})(function (require, exports) {
    "use strict";
    var Domlet_1 = require("./Domlet");
    class MaterialDomlet extends Domlet_1.Domlet {
        constructor(template, points) {
            super(template, points);
        }
        htmlElement() {
            var element = super.htmlElement(null);
            this.initMaterialElement(element);
            return element;
        }
        initMaterialElement(e) {
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
                    this.initMaterialElement(e.children[c]);
            }
        }
    }
    exports.MaterialDomlet = MaterialDomlet;
});
//# sourceMappingURL=MaterialDomlet.js.map