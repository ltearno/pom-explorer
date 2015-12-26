var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var MaterialDomlet = (function (_super) {
    __extends(MaterialDomlet, _super);
    function MaterialDomlet(template, points) {
        _super.call(this, template, points);
    }
    MaterialDomlet.prototype.htmlElement = function () {
        var element = _super.prototype.htmlElement.call(this);
        this.initMaterialElement(element);
        return element;
    };
    MaterialDomlet.prototype.initMaterialElement = function (e) {
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
    };
    return MaterialDomlet;
})(Domlet);
//# sourceMappingURL=MaterialDomlet.js.map