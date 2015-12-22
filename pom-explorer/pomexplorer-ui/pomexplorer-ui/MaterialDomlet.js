var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var MaterialDomlet = (function (_super) {
    __extends(MaterialDomlet, _super);
    function MaterialDomlet(template, points) {
        _super.call(this, template, points);
        this.init(this.element);
    }
    MaterialDomlet.prototype.init = function (e) {
        if (e == null)
            return;
        try {
            componentHandler.upgradeElement(e);
        }
        catch (ex) {
        }
        for (var c in e.children)
            this.init(e.children[c]);
    };
    return MaterialDomlet;
})(Domlet);
//# sourceMappingURL=MaterialDomlet.js.map